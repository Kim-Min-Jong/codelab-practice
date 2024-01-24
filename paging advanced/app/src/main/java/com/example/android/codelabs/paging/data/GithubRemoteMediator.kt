package com.example.android.codelabs.paging.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.example.android.codelabs.paging.api.GithubService
import com.example.android.codelabs.paging.db.RepoDatabase
import com.example.android.codelabs.paging.model.Repo

// Paging 라이브러리에서는 데이터베이스를 UI에 표시해야 하는 데이터의 정보 소스로 사용한다.
// 데이터베이스에 더 이상 데이터가 없을 때마다 네트워크에 더 많은 데이터를 요청해야 한다.
// 이 작업에 도움이 되도록 Paging 3에서는 구현해야 하는 load() 메서드와 함께 RemoteMediator 추상 클래스를 정의한다.
// 네트워크에서 더 많은 데이터를 로드해야 할 때마다 이 메서드가 호출된다.
// 이 클래스는 MediatorResult 객체(다음 중 하나)를 반환한다.
@OptIn(ExperimentalPagingApi::class)
class GithubRemoteMediator(
    private val query: String,
    private val service: GithubService,
    private val repoDatabase: RepoDatabase
): RemoteMediator<Int, Repo>() {
    override suspend fun load(
        // 이전에 로드한 데이터의 끝부분(append) or 시작부분(prepend)에서 데이터를 로드하는지
        // 혹은 데이터를 처음으로 로드하는지(refresh) 나타냄
        loadType: LoadType,
        // 이전에 로드된 페이지, 목록에서 가장 최근에 액세스한 색인, 페이징 스트림을 초기화할 때 정의한 PagingConfig에 관한 정보를 제공
        state: PagingState<Int, Repo>
    ): MediatorResult {
        // ex) loadType == LoadType.APPEND 면 PagingState에서 로드된 마지막 항목을 가져옴
        TODO("Not yet implemented")
    }
}
