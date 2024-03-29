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

package com.example.android.codelabs.paging.ui

import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.map
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.example.android.codelabs.paging.Injection
import com.example.android.codelabs.paging.databinding.ActivitySearchRepositoriesBinding
import com.example.android.codelabs.paging.model.Repo
import com.example.android.codelabs.paging.model.RepoSearchResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SearchRepositoriesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySearchRepositoriesBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // get the view model
        val viewModel =
            ViewModelProvider(this, Injection.provideViewModelFactory(owner = this, context = this))
                .get(SearchRepositoriesViewModel::class.java)

        // add dividers between RecyclerView's row items
        val decoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        binding.list.addItemDecoration(decoration)

        // bind the state
        binding.bindState(
            uiState = viewModel.state,
            uiActions = viewModel.accept
        )
    }

    /**
     * Binds the [UiState] provided  by the [SearchRepositoriesViewModel] to the UI,
     * and allows the UI to feed back user actions to it.
     */
    private fun ActivitySearchRepositoriesBinding.bindState(
        // 상태를 flow로 받고있기 때문에 변경
        uiState: StateFlow<UiState>,
        uiActions: (UiAction) -> Unit
    ) {
        val repoAdapter = ReposAdapter()
        // PagingDataAdapter를 위한 세 가지 유용한 메소드
        // 1. withLoadStateHeader: 머리글만 표시하려는 경우, 목록의 시작 부분에만 항목을 추가할 수 있는 경우 사용
        // 2. withLoadStateFooter: 바닥글만 표시하려는 경우, 목록의 끝 부분에만 항목을 추가할 수 있는 경우 사용
        // 3. withLoadStateHeaderAndFooter: 머리글과 바닥글을 표시하려는 경우, 목록을 두 방향으로 모두 페이징할 수 있는 경우.
        list.adapter = repoAdapter.withLoadStateHeaderAndFooter(
            // 메인 repoAdapter에 헤더와 푸터 (맨 위, 아래)에 LoadStateAdapter를 연결
            header = ReposLoadStateAdapter { repoAdapter.retry() },
            footer = ReposLoadStateAdapter { repoAdapter.retry() }
        )

        bindSearch(
            uiState = uiState,
            onQueryChanged = uiActions
        )
        bindList(
            repoAdapter = repoAdapter,
            uiState = uiState,
            onScrollChanged = uiActions,
            header = ReposLoadStateAdapter { }
        )
    }

    private fun ActivitySearchRepositoriesBinding.bindSearch(
        uiState: StateFlow<UiState>,
        onQueryChanged: (UiAction.Search) -> Unit
    ) {
        searchRepo.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                updateRepoListFromInput(onQueryChanged)
                true
            } else {
                false
            }
        }
        searchRepo.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                updateRepoListFromInput(onQueryChanged)
                true
            } else {
                false
            }
        }
        lifecycleScope.launch {
            uiState.map { it.query }
                .distinctUntilChanged()
                .collect(searchRepo::setText)
        }
    }

    private fun ActivitySearchRepositoriesBinding.updateRepoListFromInput(onQueryChanged: (UiAction.Search) -> Unit) {
        searchRepo.text.trim().let {
            if (it.isNotEmpty()) {
                list.scrollToPosition(0)
                onQueryChanged(UiAction.Search(query = it.toString()))
            }
        }
    }

    private fun ActivitySearchRepositoriesBinding.bindList(
        header: ReposLoadStateAdapter,
        repoAdapter: ReposAdapter,
        uiState: StateFlow<UiState>,
        onScrollChanged: (UiAction.Scroll) -> Unit
    ) {
        // 재시도 버튼 이벤트 정의
        retryButton.setOnClickListener { repoAdapter.retry() }

        // Paging 라이브러리가 목록 스크롤을 자동으로 처리하지만, 사용자가 현재 쿼리의 목록을 스크롤했는지를 나타내는 신호로 OnScrollListener가 여전히 필요
        list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy != 0) onScrollChanged(UiAction.Scroll(currentQuery = uiState.value.query))
            }
        })

        // shouldScrollToTop -  bool 플래그를 만들도록 파이프라인을 설정
        // 그러면, collect할 수 있는 두 흐름 즉, PagingData Flow 및 shouldScrollToTop Flow가 생김
        val notLoading = repoAdapter.loadStateFlow
//            // LoadState가 refresh(페이징 데이터가 바뀔때)일때만 flow를 방출한다(emit)
//            .distinctUntilChangedBy { it.source.refresh }
//            // refresh가 NotLoading 상태일 때는 true
//            .map { it.source.refresh is LoadState.NotLoading }
            .asRemotePresentationState()
            .map { it == RemotePresentationState.PRESENTED }


        // uiState에서 상태값 가져옴
        val hasNotScrolledForCurrentSearch = uiState
            .map { it.hasNotScrolledForCurrentSearch }
            .distinctUntilChanged()

        // 데이터 flow 결합
        val shouldScrollToTop = combine(
            notLoading,
            hasNotScrolledForCurrentSearch,
            Boolean::and
        ).distinctUntilChanged()

        lifecycleScope.launch {
            // 스크롤이 필요한 상태면 맨위로 보내겠다.
            shouldScrollToTop.collect { shouldScroll ->
                if (shouldScroll) list.scrollToPosition(0)
            }
        }

        // 빈 목록 시 메시지 표시
        // 목록이 로드되는 시점을 파악하기 위해 PagingDataAdapter.loadStateFlow 속성을 사용
        // 이 Flow는 로드 상태가 변경될 때마다 CombinedLoadStates 객체를 통해 메시지를 표시
        lifecycleScope.launch {
            repoAdapter.loadStateFlow.collect { loadState ->

                header.loadState = loadState.mediator
                    ?.refresh
                    ?.takeIf { it is LoadState.Error && repoAdapter.itemCount > 0 }
                    ?: loadState.prepend

                // CombinedLoadStates의 refresh 상태가 NotLoading 및 adapter.itemCount == 0인 경우 목록이 비어 있음
                val isListEmpty =
                    loadState.refresh is LoadState.NotLoading && repoAdapter.itemCount == 0
                // 상태에 따라 UI visibility 전환
                emptyList.isVisible = isListEmpty
                list.isVisible = !isListEmpty

                // 데이터 로딩 상태에 따른 Ui 상태 변경 처리
                progressBar.isVisible = loadState.mediator?.refresh is LoadState.Loading
                // 초기 로딩이나 새로 고침시 프로그레스 보여주기
                list.isVisible =  loadState.source.refresh is LoadState.NotLoading || loadState.mediator?.refresh is LoadState.NotLoading
                // 초기 로딩이나 새로 고침 실패 시 재시도 버튼 보여주기
                retryButton.isVisible = loadState.mediator?.refresh is LoadState.Error && repoAdapter.itemCount == 0

                // 에러 상황 발생 마다 toast 메세지 띄우기
                val errorState = loadState.source.append as? LoadState.Error
                    ?: loadState.source.prepend as? LoadState.Error
                    ?: loadState.append as? LoadState.Error
                    ?: loadState.prepend as? LoadState.Error

                errorState?.let {
                    Toast.makeText(
                        this@SearchRepositoriesActivity,
                        "\uD83D\uDE28 Wooops ${it.error}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun ActivitySearchRepositoriesBinding.showEmptyList(show: Boolean) {
        emptyList.isVisible = show
        list.isVisible = !show
    }

}
