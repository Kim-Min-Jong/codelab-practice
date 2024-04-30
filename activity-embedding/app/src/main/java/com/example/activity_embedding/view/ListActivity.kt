/*
 * Copyright 2023 Google LLC
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

package com.example.activity_embedding.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.window.embedding.SplitAttributes
import androidx.window.embedding.SplitController
import com.example.activity_embedding.util.ItemAdapter
import com.example.activity_embedding.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigationrail.NavigationRailView

/**
 * The list portion of a list-detail layout.
 */
class ListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        val listRecyclerView: RecyclerView = findViewById(R.id.listRecyclerView)
        val arraySize = 10
        listRecyclerView.adapter = ItemAdapter(
            Array(arraySize) { i ->
                if (i == arraySize - 1) "Summary"
                else "Item ${(i + 1)}"
            }
        )
    }

    // 탐색 메뉴 <-> 탐색 레일 간 전환을 처리 하는 메소드
    fun setWiderScreenNavigation(useNavRail: Boolean) {
        val navRail: NavigationRailView = findViewById(R.id.navigationRailView)
        val bottomNav: BottomNavigationView = findViewById(R.id.bottomNavigationView)

        if (useNavRail) {
            navRail.isVisible = true
            bottomNav.isVisible = false
        } else {
            navRail.isVisible = false
            bottomNav.isVisible = true
        }
    }

    // 탐색 메뉴가 생길 시 Split 속성 값에 대해 재정의
    /*
    다음 코드에서 액티비티가 기본 제약 조건이 충족되는지,
    즉 너비가 840dp보다 크고 최소 너비가 600dp보다 큰지 확인
    조건이 충족되면 액티비티가 이중 창 레이아웃에 삽입되고 앱은 하단 탐색 메뉴가 아닌 탐색 레일을 사용
    충족되지 않으면 액티비티가 하단 탐색 메뉴와 함께 전체 화면으로 표시
     */
    fun setSplitAttributesCalculator() {
        SplitController.getInstance(this).setSplitAttributesCalculator { params ->
            // 기본 값이 만족되있을 경우 -> 일반적인 경우
            if (params.areDefaultConstraintsSatisfied) {
                setWiderScreenNavigation(true)
                return@setSplitAttributesCalculator params.defaultSplitAttributes
            } else {
                setWiderScreenNavigation(false)
                // Split 속성을 EXPAND로 바꿔 확장 시킴
                SplitAttributes.Builder()
                    .setSplitType(SplitAttributes.SplitType.SPLIT_TYPE_EXPAND)
                    .build()
            }

        }
    }
}
