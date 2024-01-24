package com.example.android.codelabs.paging.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.android.codelabs.paging.api.GithubService
import com.example.android.codelabs.paging.api.IN_QUALIFIER
import com.example.android.codelabs.paging.db.RemoteKeys
import com.example.android.codelabs.paging.db.RepoDatabase
import com.example.android.codelabs.paging.model.Repo
import java.io.IOException
import retrofit2.HttpException

// Paging 라이브러리에서는 데이터베이스를 UI에 표시해야 하는 데이터의 정보 소스로 사용한다.
// 데이터베이스에 더 이상 데이터가 없을 때마다 네트워크에 더 많은 데이터를 요청해야 한다.
// 이 작업에 도움이 되도록 Paging 3에서는 구현해야 하는 load() 메서드와 함께 RemoteMediator 추상 클래스를 정의한다.
// 네트워크에서 더 많은 데이터를 로드해야 할 때마다 이 메서드가 호출된다.
// 이 클래스는 MediatorResult 객체(다음 중 하나)를 반환한다.

// GithubPagingSource를 대체함
@OptIn(ExperimentalPagingApi::class)
class GithubRemoteMediator(
    private val query: String,
    private val service: GithubService,
    private val repoDatabase: RepoDatabase
) : RemoteMediator<Int, Repo>() {
    override suspend fun load(
        // 이전에 로드한 데이터의 끝부분(append) or 시작부분(prepend)에서 데이터를 로드하는지
        // 혹은 데이터를 처음으로 로드하는지(refresh) 나타냄
        loadType: LoadType,
        // 이전에 로드된 페이지, 목록에서 가장 최근에 액세스한 색인, 페이징 스트림을 초기화할 때 정의한 PagingConfig에 관한 정보를 제공
        state: PagingState<Int, Repo>
    ): MediatorResult {
        // ex) loadType == LoadType.APPEND 면 PagingState에서 로드된 마지막 항목을 가져옴

        /**
         * GithubRemoteMediator.load() 메서드를 구현하는 방법
         *
         * 1. LoadType을 기반으로 네트워크에서 로드해야 하는 페이지를 확인
         * 2. 네트워크 요청을 트리거
         * 3. 네트워크 요청이 완료된 후 수신된 저장소 목록이 비어 있지 않으면 다음 작업을 실행
         * 4. 모든 Repo의 RemoteKeys를 계산
         * 5. 새로운 쿼리(loadType = REFRESH)인 경우 데이터베이스를 지움
         * 6. RemoteKeys 및 Repos를 데이터베이스에 저장
         * 7. MediatorResult.Success(endOfPaginationReached = false)를 반환
         * 8. 저장소 목록이 비어 있는 경우 MediatorResult.Success(endOfPaginationReached = true)를 반환, 데이터를 요청하는 중에 오류가 발생하면 MediatorResult.Error를 반환.
         */
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                // remote key가 null 이면 anchorPosistion도 null 이였음로 초기 페이지를 로딩
                remoteKeys?.nextKey?.minus(1) ?: GITHUB_STARTING_PAGE_INDEX
            }

            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                // remoteKey가 unll 이면, 새로고침 결과가 아직 db에 없는 것
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached =  remoteKeys != null)
                prevKey
            }

            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                // remote key가 null 이면 새로고침 결과가 아직 db에 없는 것
                // RemoteKeys가 null이 아니게 되면 Paging이 이 메서드를 호출하므로 endOfPaginationReached = false와 함께 Success를 반환할 수 있음
                // remoteKeys는 null이 아니지만 nextKey는 null인 경우 추가할 수 있는 페이지의 끝에 도달했다는 의미
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }

        val apiQuery = query + IN_QUALIFIER

        try {
            // 데이터 가져오기
            val apiResponse = service.searchRepos(
                apiQuery,
                page,
                state.config.pageSize
            )

            // repos list
            val repos = apiResponse.items
            // 페이지의 끝에 도달했는 지 확인 -> 가져온게 비어있음
            val endOfPaginationReached = repos.isEmpty()
            // 트랜잭션 시작 - db작업
            repoDatabase.withTransaction {
                // 새로고침 상태라면 모든 정보 초기화
                if (loadType == LoadType.REFRESH) {
                    repoDatabase.run {
                        remoteKeysDao().clearRemoteKeys()
                        reposDao().clearRepos()
                    }
                }

                // GithubPagingSource를 대체 하므로 동일한 로직
                val prevKey = if (page == GITHUB_STARTING_PAGE_INDEX) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                // db에 페이지 키 값을 넣을 객체를 생성
                val keys = repos.map {
                    RemoteKeys(repoId = it.id, prevKey = prevKey, nextKey = nextKey)
                }

                // db에 데이터 주입
                repoDatabase.run {
                    remoteKeysDao().insertAll(keys)
                    reposDao().insertAll(repos)
                }
            }
            // 성공 상태를 반환
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }
    }

    // LoadType.APPEND
    // 현재 로드된 데이터의 끝 부분에서 데이터를 로드해야하는 경우
    // -> 데이터베이스의 마지막 항목을 기반으로 네트워크 페이지 키를 계산해야함
    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int ,Repo>): RemoteKeys? {
        // 데이터가 있는 마지막 페이지를 가져옴
        // 마지막 페이지에서 마지막 항목을 가져옴
        return state.pages.lastOrNull() {
            it.data.isNotEmpty()
        }?.data?.lastOrNull()?.let { repo ->
            // 마지막 항목의 키를 가져옴
            repoDatabase.remoteKeysDao().remoteKeysRepoId(repo.id)
        }
    }

    // LoadType.PREPEND
    // 현재 로드된 데이터 세트의 시작 부분에서 데이터를 로드해야 하는 경우
    // 데이터베이스의 첫 번째 항목을 기반으로 네트워크 페이지 키를 계산해야함
    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, Repo>): RemoteKeys? {
        // 데이터가 있는 첫 페이지를 가져옴
        // 첫 페이지에서 첫 항목을 가져옴
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { repo ->
                // 처음 항목의 키를 가져옴
                repoDatabase.remoteKeysDao().remoteKeysRepoId(repo.id)
            }
    }

    // LoadType.REFRESH
    // 데이터를 첫 로드 or PagingDataAdapter.refresh()가 호출되는 경우 호출
    // 데이터를 로드하기 위한 참조 지점은 state.anchorPosition이 됨
    // 첫 로드인 경우 anchorPosition은 null
    // refresh()가 호출되면 anchorPosition이 표시된 목록에 처음 표시되는 위치이므로
    // 그 특정 항목이 포함된 페이지를 로드해야함
    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, Repo>
    ): RemoteKeys? {
        // anchorPosition부터 PagingData를 로딩 시작
        return state.anchorPosition?.let { position ->
            // 가장 가까운 아이템을 anchor로 하여 키를 찾음
            state.closestItemToPosition(position)?.id?.let { repoId ->
                repoDatabase.remoteKeysDao().remoteKeysRepoId(repoId)
            }
        }
    }
}



// base page index
private const val GITHUB_STARTING_PAGE_INDEX = 1


