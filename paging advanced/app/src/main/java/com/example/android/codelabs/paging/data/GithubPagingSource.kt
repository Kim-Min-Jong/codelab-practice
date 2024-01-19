package com.example.android.codelabs.paging.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.android.codelabs.paging.api.GithubService
import com.example.android.codelabs.paging.api.IN_QUALIFIER
import com.example.android.codelabs.paging.data.GithubRepository.Companion.NETWORK_PAGE_SIZE
import com.example.android.codelabs.paging.model.Repo
import okio.IOException
import retrofit2.HttpException


// base page index
private const val GITHUB_STARTING_PAGE_INDEX = 1

// PagingSource 구현에서는 데이터 소스를 정의하고 이 소스에서 데이터를 가져오는 방법을 정의

class GithubPagingSource(
    private val service: GithubService,
    private val query: String
): PagingSource<Int, Repo>() {
    // PagingSource를 빌드하려면 다음 항목을 정의
    //
    // 페이징 키의 유형: 여기서는 GitHub API에서 페이지에 1을 기반으로 하는 색인 번호를 사용하므로 유형이 Int
    // 로드된 데이터의 유형: 여기서는 Repo 항목을 로드
    // 데이터를 가져오는 위치: GithubService에서 데이터를 가져옴.
    // 데이터 소스는 특정 쿼리에 한정되므로 쿼리 정보도 GithubService에 전달해야함
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Repo> {
        // 로딩해올 위치, null 이면 1페이지를 가져옴
        val position = params.key ?: GITHUB_STARTING_PAGE_INDEX
        // 가져올 정보의 쿼리
        val apiQuery = query + IN_QUALIFIER
        // LoadResult를 가져와 정보를 나타낼 준비
        return try {
            // 쿼리와 페이지수, 가져올 정보 사이즈를 토대로 결과값을 가져옴
            val response = service.searchRepos(
                apiQuery,
                position,
                params.loadSize
            )
            // 검색된 Repo 리스트
            val repos = response.items
            // 반환 결과가 없다면 다음 키값을 null로 처리, 있다면 다음 페이지를 가져올수 있도록 설정
            val nextKey = if(repos.isEmpty()) {
                null
            } else {
                // 기본적으로 초기 로드 크기는 3 * 페이지 크기이다.
                // 이 상태로 Paging을 사용하면 목록이 처음 로드될 때 사용자에게 충분한 항목이 표시되며 사용자가 로드된 항목을 지나 스크롤하지 않으면 너무 많은 네트워크 요청이 트리거되지 않는다.
                // PagingSource 구현에서 다음 키를 계산할 때 이 내용을 고려해야 합니다.
                position + (params.loadSize / NETWORK_PAGE_SIZE)
            }
            // 결과 반환
            LoadResult.Page(
                data = repos,
                // 1페이지라면 이전 페이지는 없으니 null 처리, 아니면 -1을 통해 이전 페이지로 가도록 처리
                prevKey = if(position == GITHUB_STARTING_PAGE_INDEX) null else position - 1,
                nextKey = nextKey
            )
        } catch (exception: IOException) {
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            return LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Repo>): Int? {
        // anchorPosition - 최근에 접근된 인덱스
        // 최근에 접근된 인덱스에서 +-1 중 하나를 refresh key로 설정
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)

        }
    }

}
