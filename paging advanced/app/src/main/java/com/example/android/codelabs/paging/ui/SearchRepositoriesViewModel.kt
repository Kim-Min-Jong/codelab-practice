/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.codelabs.paging.ui

import android.os.Build.VERSION_CODES.P
import android.os.Build.VERSION_CODES.S
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import com.example.android.codelabs.paging.data.GithubRepository
import com.example.android.codelabs.paging.model.Repo
import com.example.android.codelabs.paging.model.RepoSearchResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel for the [SearchRepositoriesActivity] screen.
 * The ViewModel works with the [GithubRepository] to get the data.
 */
class SearchRepositoriesViewModel(
    private val repository: GithubRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    /**
     * Stream of immutable states representative of the UI.
     */
    // Paging3를 이용하면 LiveData로 변환할 필요가 없음, 단 StateFlow로 관리
    // 참고: PagingData Flow를 다른 Flows와 함께 사용하거나 결합X
    // 내보낸 PagingData는 각각 독립적으로 사용되어야한다.
    // 또한 shareIn 및 stateIn 같은 연산자를 PagingData Flows에 사용해서는 안 된다.
    val state: StateFlow<UiState>

    val pagingDataFlow: Flow<PagingData<UiModel>>


    /**
     * Processor of side effects from the UI which in turn feedback into [state]
     */
    // 뷰모델의 요청에 대한 하나의 접근 점(진입점)
    // 다음과 같은 동작을 수행해야함
    // 1. 진입점을 관심 있는 유형이 포함된 스트림으로 변환
    // 2. 그러한 스트림을 변환합니다.
    // 3. 스트림을 다시 StateFlow<UiState>로 결합
    val accept: (UiAction) -> Unit

    init {
        val queryLiveData =
            MutableLiveData(savedStateHandle.get(LAST_SEARCH_QUERY) ?: DEFAULT_QUERY)

        // ViewModel의  accept를 UiAction으로 변환하는 작업

        // 초기 쿼리 (저장된 쿼리가 있으면 그것으로 설정 없으면 디폴트값)
        val initialQuery: String = savedStateHandle.get(LAST_SEARCH_QUERY) ?: DEFAULT_QUERY
        // 마지막으로 스크롤시 나왔던 쿼리 (사용자가 목록과 상호작용한 마지막 검색어)
        val lastQueryScrolled: String = savedStateHandle.get(LAST_QUERY_SCROLLED) ?: DEFAULT_QUERY
        // 데이터 상태 스트림
        val actionStateFlow = MutableSharedFlow<UiAction>()

        // Flow를 특정 UiAction 유형으로 분할
        // UiAction.Search - 사용자가 특정 쿼리를 입력하는 각 경우
        // UiAction.Scroll - 사용자가 포커스가 지정된 특정 쿼리와 함께 목록을 스크롤하는 각 경우
        // 1
        val searches = actionStateFlow
            .filterIsInstance<UiAction.Search>()
            .distinctUntilChanged()
            .onStart {
                emit(UiAction.Search(query = initialQuery))
            }
        // 2
        val queriesScrolled = actionStateFlow
            .filterIsInstance<UiAction.Scroll>()
            .distinctUntilChanged()
            // Flow가 최종적으로 사용될 떄, flatmaplatest 연산자를 사용되기 떄문에 필요
            // 업스트림에서 내보낼 때 마다, flatmaplatest는 마지막으로 작업한 flow를 취소하고, 새 스트림을 기반으로 작업을 시작하는데,
            // 여기서 사용자가 스크롤한 마지막 쿼리의 값이 손실됨
            // 따라서 새 쿼리가 수신되더라도 값이 손실되지 않도록 replay = 1인 Flow를 사용하여 마지막 값을 캐시
            .shareIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
                replay = 1
            )
            .onStart { emit(UiAction.Scroll(currentQuery = lastQueryScrolled)) }


        // pagingData 초기화
        pagingDataFlow = searches
            // 새로운 검색어마다 Pager를 새로 만들어야 하므로 searches 스트림에 flatmapLatest 연산자 사용
            .flatMapLatest { searchRepo(queryString = it.query) }
            // PagaingData가 viewModelScope 유지 중에는 cache되도록..
            .cachedIn(viewModelScope)

        // 상태 초기화 (searches, queriesScolled 를 함께 사용
        state = combine(
            searches,
            queriesScrolled,
            ::Pair
        ).map { (search, scroll) ->
            // search 값과 scroll값을 통해 UiState를 제어
            UiState(
                query = search.query,
                lastQueryScrolled = scroll.currentQuery,
                // 검색쿼리와 스크롤쿼리가 맞지 않으면 사용자가 스크롤을 했다고 판단
                hasNotScrolledForCurrentSearch = search.query != scroll.currentQuery
            )
        }
            // flow에 할당
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
                initialValue = UiState()
            )
        accept = { action ->
            // action을 통해 새ㅇ로운 값을 스트림으로 보냄 (액티비티로..)
            viewModelScope.launch { actionStateFlow.emit(action) }
        }
    }


    override fun onCleared() {
        savedStateHandle[LAST_SEARCH_QUERY] = state.value.query
        // 스크롤도 복원되애하는 항목이므로 save
        savedStateHandle[LAST_QUERY_SCROLLED] = state.value.lastQueryScrolled
        super.onCleared()
    }

    private fun searchRepo(queryString: String): Flow<PagingData<UiModel>> =
        repository.getSearchResultStream(queryString)
            // Flow<PagingData<Repo>>를 반환하므로 먼저 각 Repo를 UiModel.RepoItem으로 변환하는 작업
            .map { pagingData -> pagingData.map { UiModel.RepoItem(it) } }
            .map {
                // 구분자 삽입을 위해 flow가 표시 될때마다, agingData.insertSeparators()를 호출
                // 이 메소드는 전후 요소가 지정된 경우 각 원본 요소가 포함된 데이터를 구분자와 함께 반환한다.
                // 리스트의 처음과 끝에서는 각 전후 요소는 null이다.(없기 때문에) + 구분자를 만들 필요가 없을 때도 null을 반환한다.
                it.insertSeparators { before, after ->
                    // 리스트의 마지막일 때
                    if (after == null) {
                        return@insertSeparators null
                    }
                    // 리스트의 시작일 때
                    if (before == null) {
                        return@insertSeparators UiModel.SeparatorItem("${after.roundedStarCount}0.000+ stars")
                    }
                    // 두 항목 사이 일 때
                    if(before.roundedStarCount > after.roundedStarCount) {
                        // 모델에 구분자 표시할 텍스트 전달
                        if (after.roundedStarCount >= 1) {
                            UiModel.SeparatorItem("${after.roundedStarCount}0.000+ stars")
                        } else {
                            UiModel.SeparatorItem("< 10.000+ stars")
                        }
                    } else {
                        null
                    }
                }
            }
}

// This is outside the ViewModel class, but in the same file
private const val LAST_QUERY_SCROLLED: String = "last_query_scrolled"

// 삭제
//private val UiAction.Scroll.shouldFetchMore
//    get() = visibleItemCount + lastVisibleItemPosition + VISIBLE_THRESHOLD >= totalItemCount

// 상태 관리를 위한 sealed class
sealed class UiAction {
    data class Search(val query: String) : UiAction()
    data class Scroll(
        // 스크롤 작업을 특정 쿼리와 연결 시키기 위한 프로퍼티 설정
        val currentQuery: String
    ) : UiAction()
}

// 구분자와 데이터를 구분할 sealed class
sealed class UiModel {
    data class RepoItem(val repo: Repo): UiModel()
    data class SeparatorItem(val description: String): UiModel()
}
// 10000개의 좋아요 숫자로 구분자를 구분짓기 위한 확장함수
private val UiModel.RepoItem.roundedStarCount: Int
    get() = this.repo.stars / 10_000

data class UiState(
    // 디폴트 검색값 설정
    val query: String = DEFAULT_QUERY,
    // 마지막으로 수행 된 검색값 설정
    val lastQueryScrolled: String = DEFAULT_QUERY,
    // 입력된 새 쿼리와 관련해 리스트의 상단으로 스크롤 하면 첫번째 결과가 표시되는데
    // 페이징 데이터가 여러번 내보내 질 수도 있으니
    // 사용자가 스크롤을 시작하지 않은 경우에만 상단으로 스크롤 할 수 있도록 변수 설정
    val hasNotScrolledForCurrentSearch: Boolean = false
)

private const val VISIBLE_THRESHOLD = 5
private const val LAST_SEARCH_QUERY: String = "last_search_query"
private const val DEFAULT_QUERY = "Android"
