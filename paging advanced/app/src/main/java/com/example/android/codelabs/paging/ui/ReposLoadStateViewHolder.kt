package com.example.android.codelabs.paging.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.recyclerview.widget.RecyclerView
import com.example.android.codelabs.paging.R
import com.example.android.codelabs.paging.databinding.ReposLoadStateFooterViewItemBinding

// 로딩과 에러처리 아이템을 위한 뷰홀더 클래스
class ReposLoadStateViewHolder(
    private val binding: ReposLoadStateFooterViewItemBinding,
    // 재시도 버튼을 누를 때 실행할 콜백(람다)
    retry: () -> Unit
): RecyclerView.ViewHolder(binding.root) {

    init {
        // 초기화 될 때, 재시도 버튼의 동작을 정의
        binding.retryButton.setOnClickListener { retry.invoke() }
    }

    fun bind(loadState: LoadState) = with(binding){
        if(loadState is LoadState.Error) {
            errorMsg.text = loadState.error.localizedMessage
        }
        progressBar.isVisible = loadState is LoadState.Loading
        retryButton.isVisible = loadState is LoadState.Error
        errorMsg.isVisible = loadState is LoadState.Error
    }

    companion object {
        // 뷰홀더 객체 생성 (싱글턴)
        fun create(parent: ViewGroup, retry: () -> Unit): ReposLoadStateViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.repos_load_state_footer_view_item, parent, false)
            val binding = ReposLoadStateFooterViewItemBinding.bind(view)
            return ReposLoadStateViewHolder(binding, retry)
        }
    }
}
