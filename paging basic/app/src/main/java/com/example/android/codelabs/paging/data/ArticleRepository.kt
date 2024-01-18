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

package com.example.android.codelabs.paging.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.time.LocalDateTime

private val firstArticleCreatedTime = LocalDateTime.now()

/**
 * Repository class that mimics fetching [Article] instances from an asynchronous source.
 */
// PagingData를 구성하기 위해 PagingData를 앱의 다른 레이어에 전달하는 데 사용할 API에 따라 Pager 클래스의 여러 빌더 메서드 중 하나를 사용
//
// 1. Kotlin Flow - Pager.flow 사용
// 2. LiveData - Pager.liveData 사용
// 3. RxJava Flowable - Pager.flowable 사용
// 4. RxJava Observable - Pager.observable 사용

// 사용하는 PagingData 빌더에 관계없이 다음 매개변수를 전달
// 1. PagingConfig - 이 클래스는 로드 대기 시간, 초기 로드의 크기 요청 등 PagingSource에서 콘텐츠를 로드하는 방법에 관한 옵션을 설정
// 2. PagingSource를 만드는 방법을 정의하는 함수
class ArticleRepository {

    // 만들어 놓은 페이징 클래스로 페이지 목록을 보여줌
    // Paging 라이브러리는 다양한 작업을 실행함
    // 1. 메모리 내 캐시를 처리
    // 2. 사용자가 목록의 끝에 가까워지면 데이터를 요청
    fun articlePagingSource() = ArticlePagingSource()
}
