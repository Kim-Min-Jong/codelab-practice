package com.example.android.codelabs.paging.ui

import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter

// 로딩, 에러 메세지를 연결 및 보여줄 어댑터 클래스
class ReposLoadStateAdapter(
    private val retry: () -> Unit
): LoadStateAdapter<ReposLoadStateViewHolder>() {
    // loadState를 전달
    override fun onBindViewHolder(holder: ReposLoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): ReposLoadStateViewHolder {
        return ReposLoadStateViewHolder.create(parent, retry)
    }
}
