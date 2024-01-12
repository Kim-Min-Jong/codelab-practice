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

package com.example.compose.rally

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.compose.rally.ui.components.RallyTabRow
import com.example.compose.rally.ui.theme.RallyTheme

/**
 * This Activity recreates part of the Rally Material Study from
 * https://material.io/design/material-studies/rally.html
 */
class RallyActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RallyApp()
        }
    }
}

// 앱의 최상단 컴포저블에 내비게이션 컨트롤러 설정
// NavController
// compose에서 탐색을 사용할 때 중심이 되는 구성요소
// 백 스택 컴포저블 항목을 추적하고, 스택을 앞으로 이동하고, 백 스택 조작을 사용 설정하고, 대상 상태 간에 이동

@Composable
fun RallyApp() {
    RallyTheme {
        var currentScreen: RallyDestination by remember { mutableStateOf(Overview) }
        // 내비게이션 컨트롤러 설정
        // NavController는 항상 컴포저블 계층 최상위 수준에서 만들고 배치해야함
        // 그래야 NavController를 참조해야하는 컴포저블이 접근할 수 있기 때문에
        // -> 이는 상태 호이스팅 원칙을 준수하고 컨트롤러가 컴포저블 이동간 백스택을 유지하기 위한 기본 정보가 되도록 함

        // compose에서 navigation을 사용할 때는 탐색 그래프의 각 컴포저블 대상에 경로가 연결된다.
        // 경로는 컴포저블의 경로를 정의하는 문자열로 표현되고 올바른 위치로 이동할 수 있도록 navController가 안내한다.
        // 이를 암시적 딥링크라고 한다. (각 대상에는 고유 경로가 있어야한다.)
        val navController = rememberNavController()
        Scaffold(
            topBar = {
                RallyTabRow(
                    allScreens = rallyTabRowScreens,
                    onTabSelected = { screen -> currentScreen = screen },
                    currentScreen = currentScreen
                )
            }
        ) { innerPadding ->
            // Navigation의 주요 세가지 NavController, NavGraph, NavHost
            // NavController는 항상 단일 NavHost 컴포저블에 연결됨
            // NavHost는 컨테이너 역할을 하여 그래프의 현재 대상은 표시함. 여러 컴포저블 이동 간 navHost의 콘텐츠가 자동으로 재구성 됨
            // 또, NavController를 이동 가능한 컴포저블 대상을 매핑하는 NavGraph에 연결함
            // 여기서 NavHost는 가져올 수 있는 컴포저블의 모음

            NavHost(
                // NavController를 NavHost에 연결
                navController = navController,
                // navigation의 시작점을 연결
                startDestination = Overview.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                // TODO builder: NavGraphBuilder.() -> Unit - 탐색 그래프를 정의하고 만드는 일을 담당
            }
            Box(Modifier.padding(innerPadding)) {
                currentScreen.screen()
            }
        }
    }
}
