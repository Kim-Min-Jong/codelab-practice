/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.codelabs.paging.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.android.codelabs.paging.data.Article
import com.example.android.codelabs.paging.data.ArticleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel for the [ArticleActivity] screen.
 * The ViewModel works with the [ArticleRepository] to get the data.
 */
private const val ITEMS_PER_PAGE = 50

// PagingData를 구성하기 위해 PagingData를 앱의 다른 레이어에 전달하는 데 사용할 API에 따라 Pager 클래스의 여러 빌더 메서드 중 하나를 사용
//
// 1. Kotlin Flow - Pager.flow 사용
// 2. LiveData - Pager.liveData 사용
// 3. RxJava Flowable - Pager.flowable 사용
// 4. RxJava Observable - Pager.observable 사용

// 사용하는 PagingData 빌더에 관계없이 다음 매개변수를 전달
// 1. PagingConfig - 이 클래스는 로드 대기 시간, 초기 로드의 크기 요청 등 PagingSource에서 콘텐츠를 로드하는 방법에 관한 옵션을 설정
// 2. PagingSource를 만드는 방법을 정의하는 함수
class ArticleViewModel(
    repository: ArticleRepository,
) : ViewModel() {

    /**
     * Stream of [Article]s for the UI.
     * https://medium.com/androiddevelopers/things-to-know-about-flows-sharein-and-statein-operators-20e6ccb2bc74
     * 참고
     */
    // flow인데 데이터를 저장할 수 있는 flow
    val items: Flow<PagingData<Article>> = Pager(
        config = PagingConfig(
            pageSize = ITEMS_PER_PAGE,
            enablePlaceholders = false,
        ),
        pagingSourceFactory = { repository.articlePagingSource() }
    )
        .flow
        // 구성 또는 탐색 변경사항에도 페이징 상태를 유지하려면 androidx.lifecycle.viewModelScope를 전달하는 cachedIn() 메서드를 사용
        // stateIn이나 sharedIn 사용 금지 - 콜드Flow가 아니기 때문
        // https://developer.android.com/kotlin/flow/stateflow-and-sharedflow?hl=ko#sharein 참고
        .cachedIn(viewModelScope)
}
