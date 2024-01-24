package com.example.android.codelabs.paging.ui

import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.scan

// 지금까지 CombinedLoadStates에서 읽을 때는 항상 CombinedLoadStates.source에서 읽어왔음
// 그러나 RemoteMediator를 사용할 경우 CombinedLoadStates.source와 CombinedLoadStates.mediator를 모두 확인해야만 정확한 로드 정보를 가져올 수 있음
// 특히 현재는 source LoadState가 NotLoading일 때 새 쿼리의 목록 상단으로 스크롤되도록 트리거하고 있음
// 또한 새로 추가된 RemoteMediator에 NotLoading의 LoadState도 있어야 함
enum class RemotePresentationState {
    INITIAL, REMOTE_LOADING, SOURCE_LOADING, PRESENTED
}

@OptIn(ExperimentalCoroutinesApi::class)
fun Flow<CombinedLoadStates>.asRemotePresentationState(): Flow<RemotePresentationState> =
    scan(RemotePresentationState.INITIAL) { state, loadState ->
        when (state) {
            RemotePresentationState.PRESENTED -> when (loadState.mediator?.refresh) {
                is LoadState.Loading -> RemotePresentationState.REMOTE_LOADING
                else -> state
            }
            RemotePresentationState.INITIAL -> when (loadState.mediator?.refresh) {
                is LoadState.Loading -> RemotePresentationState.REMOTE_LOADING
                else -> state
            }
            RemotePresentationState.REMOTE_LOADING -> when (loadState.source.refresh) {
                is LoadState.Loading -> RemotePresentationState.SOURCE_LOADING
                else -> state
            }
            RemotePresentationState.SOURCE_LOADING -> when (loadState.source.refresh) {
                is LoadState.NotLoading -> RemotePresentationState.PRESENTED
                else -> state
            }
        }
    }
        .distinctUntilChanged()
