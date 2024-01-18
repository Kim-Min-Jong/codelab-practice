package com.example.android.codelabs.paging.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import java.time.LocalDateTime
import java.util.Collections.max


// 페이징 시 봐야할 점
// 1. UI의 데이터 요청을 올바르게 처리하여 동일한 쿼리에 여러 요청이 동시에 트리거되지 않도록한다.
// 2. 관리 가능한 양의 가져온 데이터를 메모리에 유지
// 3. 미 가져온 데이터를 보완하기 위해 추가 데이터를 가져오라는 요청을 트리거한다.

// PagingSource를 빌드하려면 다음 항목을 정의
// 1. 페이징 키의 유형: 추가 데이터를 요청하는 데 사용하는 페이지 쿼리 유형의 정의
// 2. 로드된 데이터의 유형
// 3. 데이터를 가져오는 위치: 일반적으로 데이터베이스나 네트워크 리소스, 페이지로 나눈 데이터의 다른 소스

private const val STARTING_KEY = 0
private const val LOAD_DELAY_MILLIS = 3_000L
private val firstArticleCreatedTime = LocalDateTime.now()

// 1
class ArticlePagingSource : PagingSource<Int, Article>() {
    // 2
    // 사용자가 스크롤 할 때, 더 많은 데이터를 비동기식으로 가져오기 위해 paging 라이브러리에서 load함수를 호출
    // LoadParams 객체에는 다음과 같은 정보들이 있음
    // 1. 로드할 페이지의 키
    // 2. 로드할 페이지의 크기

    // load함수는 3가지 유형을 반환
    // 1. LoadResult.Page: 로드에 성공한 경우
    // 2. LoadResult.Error: 오류가 발생한 경우
    // 3. LoadResult.Invalid: PagingSource가 더 이상 결과의 무결성을 보장할 수 없으므로 무효화되어야 하는 경우

    // LoadResult.Page에는 다음과 같은 세 가지 필수 인수
    // 1. data: 가져온 항목의 List
    // 2. prevKey: 현재 페이지 앞에 항목을 가져와야 하는 경우 load() 메서드에서 사용하는 키
    // 3. nextKey: 현재 페이지 뒤에 항목을 가져와야 하는 경우 load() 메서드에서 사용하는 키

    // 선택적 인수 두 개
    // 1. itemsBefore: 로드된 데이터 앞에 표시할 자리표시자의 수
    // 2. itemsAfter: 로드된 데이터 뒤에 표시할 자리표시자의 수
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        // 첫번째 페이지의 키가 될 변수
        val start = params.key ?: STARTING_KEY
        // 보여 줄 페이지의 범위
        val range = start until (start + params.loadSize)

        return LoadResult.Page(
            // 페이지의 데이터를 생성
            data = range.map { number ->
                // 범위 개수에 맞게 객체를 생성
                Article(
                    id = number,
                    title = "Article $number",
                    description = "This describes article $number",
                    created = firstArticleCreatedTime.minusDays(number.toLong())
                )
            },
            // 이전에서 가져올 떄 필요한 키
            prevKey = when (start) {
                STARTING_KEY -> null
                else -> ensureValidKey(key = range.first - params.loadSize)
            },
            // 다음에서 가져올 때 필요한 키
            nextKey =range.last + 1
        )
    }

    // 3
    // paging 라이브러리가 UI 관련 항목을 새로고침할 때 호출 되는 함수
    // 'PagingSource의 데이터가 변경'

    // PagingSource의 기본 데이터가 변경되었으며 UI에서 업데이트해야 하는 이 상황을 무효화라고 합니다. 무효화되면 Paging 라이브러리가 데이터를 새로고침할 새 PagingSource를 만들고 새 PagingData를 내보내 UI에 알림
    // 무효화가 발생하는 이유
    // 1. PagingAdapter에서 refresh()를 호출
    // 2. PagingSource에서 invalidate()를 호출

    // 이 메소드에서 반환된 키는 LoadParams 인수를 통해 새 PagingSource의 다음 load() 메서드 호출에 전달
    override fun getRefreshKey(state: PagingState<Int, Article>): Int? {
        // 이전과 가장 가까웠던 페이지 (기준)
        val anchorPosition = state.anchorPosition ?: return null
        val article = state.closestItemToPosition(anchorPosition) ?: return null
        return ensureValidKey(key = article.id - (state.config.pageSize / 2))
    }

    // 페이징 키가 유효한 지 확인하는 함수
    private fun ensureValidKey(key: Int) = max(STARTING_KEY, key)
}
