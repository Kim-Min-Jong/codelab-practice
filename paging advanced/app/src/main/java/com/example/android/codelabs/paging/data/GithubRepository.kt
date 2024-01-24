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

package com.example.android.codelabs.paging.data

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.android.codelabs.paging.api.GithubService
import com.example.android.codelabs.paging.api.IN_QUALIFIER
import com.example.android.codelabs.paging.db.RepoDatabase
import com.example.android.codelabs.paging.model.Repo
import com.example.android.codelabs.paging.model.RepoSearchResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import retrofit2.HttpException
import java.io.IOException

// GitHub page API is 1 based: https://developer.github.com/v3/#pagination
private const val GITHUB_STARTING_PAGE_INDEX = 1

/**
 * Repository class that works with local and remote data sources.
 */
// 현재 상태는 GithubService에서 데이터를 flow로 가져와 viewModel로 전달함
// 전달한 데이터는 다시 LiveData로 바꿔 UI로 출력
// 시된 목록의 끝에 도달하여 네트워크에서 더 많은 데이터가 로드될 때마다
// Flow<RepoSearchResult>에는 최신 데이터 외에 쿼리 결과 이전에 가져온 데이터의 전체 목록이 포함되는 문제 발생
// PagingData 처리를 통해 이러한 문제들을 해결
// -> 결과를 모두 캡슐화 (LoadResult 형태)
class GithubRepository(
    private val service: GithubService,
    // db 엑세스를 위한 생성자 설정
    private val database: RepoDatabase
) {


    // PagingData를 구성하기 위해 PagingData를 앱의 다른 레이어에 전달하는 데 사용할 API에 따라 Pager 클래스의 여러 빌더 메서드 중 하나를 사용
    //
    // 1. Kotlin Flow - Pager.flow 사용
    // 2. LiveData - Pager.liveData 사용
    // 3. RxJava Flowable - Pager.flowable 사용
    // 4. RxJava Observable - Pager.observable 사용

    // 사용하는 PagingData 빌더에 관계없이 다음 매개변수를 전달
    // 1. PagingConfig - 이 클래스는 로드 대기 시간, 초기 로드의 크기 요청 등 PagingSource에서 콘텐츠를 로드하는 방법에 관한 옵션을 설정
    // 2. PagingSource를 만드는 방법을 정의하는 함수
    @OptIn(ExperimentalPagingApi::class)
    fun getSearchResultStream(query: String): Flow<PagingData<Repo>> {
        // 이름으로 저장소를 검색하려면 %를 쿼리 문자열의 시작과 끝에 추가
        val dbQuery = "%${query.replace(' ', '%')}%"
        // reposDao.reposByName을 호출할 때 PagingSource를 가져옴
        // 데이터베이스에서 변경할 때마다 PagingSource가 무효화되므로 PagingSource의 새 인스턴스를 가져오는 방법을 Paging에 알려야함
        // 이렇게 하기 위해 데이터베이스 쿼리를 호출하는 함수를 만듦
        val pagingSourceFactory = { database.reposDao().reposByName(dbQuery) }
        return Pager(
            // 로딩 대기시간, 초기 로딩 크기 결정등 설정하는 매개변수
            // 기본 적으로 Paging은 로드하는 모든 페이지를 메모리에 유지
            // 사용자 스크롤시 메모리를 낭비하지 않으려면 maxSize를 설정 (여기선 설정 X)
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                // 로딩되지 않은 컨텐츠의 미리보기를 할 지
                enablePlaceholders = false
            ),
            // Paging에 들어갈 데이터를 입력 - PagingSource를 받아야함
            pagingSourceFactory = pagingSourceFactory,
            // PagingSource를 대체해서 들어감
            remoteMediator = GithubRemoteMediator(
                query,
                service,
                database
            )
        ).flow
    }

    companion object {
        const val NETWORK_PAGE_SIZE = 50
    }
}
