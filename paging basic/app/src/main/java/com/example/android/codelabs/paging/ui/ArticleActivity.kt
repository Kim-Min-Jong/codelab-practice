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

package com.example.android.codelabs.paging.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android.codelabs.paging.Injection
import com.example.android.codelabs.paging.databinding.ActivityArticlesBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ArticleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityArticlesBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Get the view model
        val viewModel by viewModels<ArticleViewModel>(
            factoryProducer = { Injection.provideViewModelFactory(owner = this) }
        )

        val items = viewModel.items
        val articleAdapter = ArticleAdapter()

        binding.bindAdapter(articleAdapter = articleAdapter)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // PagingDataAdapter.loadStateFlow를 통해 PagingDataAdapter를 통한 CombinedLoadStates에 액세스할 수 있음
                articleAdapter.loadStateFlow.collect {
                    // CombinedLoadStates 인스턴스는 데이터를 로드하는 Paging 라이브러리에 있는 모든 구성요소의 로드 상태를 설명함
                    // CombinedLoadStates.source는 LoadStates 유형으로, 세 가지 유형의 LoadState 필드가 있다
                    //
                    // LoadStates.append: 사용자의 현재 위치 후에 가져오는 항목(리스트)의 LoadState용
                    // LoadStates.prepend: 사용자의 현재 위치 전에 가져오는 항목의 LoadState용
                    // LoadStates.refresh: 초기 로드의 LoadState용

                    // 각 LoadState 자체는 다음 중 하나일 수 있음
                    // LoadState.Loading: 항목(리스트)을 로드하고 있음
                    // LoadState.NotLoading: 항목을 로드하고 있지 않음
                    // LoadState.Error: 로드 오류가 발생

                    binding.prependProgress.isVisible = it.source.prepend is LoadState.Loading
                    binding.appendProgress.isVisible = it.source.append is LoadState.Loading
                }
            }
        }

        // Collect from the Article Flow in the ViewModel, and submit it to the
        // ListAdapter.
        lifecycleScope.launch {
            // We repeat on the STARTED lifecycle because an Activity may be PAUSED
            // but still visible on the screen, for example in a multi window app
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // 새 pagingData 인스턴스를 내보낼 때
                // 이전 pagingData 내보내기에 관한 컬렉션이 취소되도록
                // pagingData Flow에서 collectLatest를 사용
                items.collectLatest {
                    // PagingDataAdapter는 submitData 를 사용
                    articleAdapter.submitData(it)
                }
            }
        }
    }
}

/**
 * Sets up the [RecyclerView] and binds [ArticleAdapter] to it
 */
private fun ActivityArticlesBinding.bindAdapter(articleAdapter: ArticleAdapter) {
    list.adapter = articleAdapter
    list.layoutManager = LinearLayoutManager(list.context)
    val decoration = DividerItemDecoration(list.context, DividerItemDecoration.VERTICAL)
    list.addItemDecoration(decoration)
}
