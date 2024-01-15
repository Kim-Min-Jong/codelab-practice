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
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.compose.rally.ui.accounts.AccountsScreen
import com.example.compose.rally.ui.bills.BillsScreen
import com.example.compose.rally.ui.components.RallyTabRow
import com.example.compose.rally.ui.overview.OverviewScreen
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
        // 내비게이션 컨트롤러 설정
        // NavController는 항상 컴포저블 계층 최상위 수준에서 만들고 배치해야함
        // 그래야 NavController를 참조해야하는 컴포저블이 접근할 수 있기 때문에
        // -> 이는 상태 호이스팅 원칙을 준수하고 컨트롤러가 컴포저블 이동간 백스택을 유지하기 위한 기본 정보가 되도록 함

        // compose에서 navigation을 사용할 때는 탐색 그래프의 각 컴포저블 대상에 경로가 연결된다.
        // 경로는 컴포저블의 경로를 정의하는 문자열로 표현되고 올바른 위치로 이동할 수 있도록 navController가 안내한다.
        // 이를 암시적 딥링크라고 한다. (각 대상에는 고유 경로가 있어야한다.)
        val navController = rememberNavController()

        // 81번 라인의 2번 문제
        // 기존에는 currentScreen 이라는 변수로 탭 UI로 조정했지만
        // navigation으로 바꾸면서 사용하지 않게 되어, 탭 Ui를 조정할 수 없게됨
        // navigation을 통해 찾아야함 그렇기에 각 시점에 현재 표시되 대상이 무엇인지를 알아야함
        // 그 후, 변경 될 떄마다 TabRow를 업데이트 해야함

        // 각 시점에 현재 표시되 대상이 무엇인지를 알기 위해 State 형식의 실시간 상태를 알아야함
        val currentBackStack by navController.currentBackStackEntryAsState()
        // NavDestination 객체를 반환하는데 여기서 어느 컴포저블이 표시되어 있는 지 확인해야함
        val currentDestination = currentBackStack?.destination
        // 일종의 id 값으로 탭의 id와 비교해서 찾음
        val currentScreen = rallyTabRowScreens.find { it.route == currentDestination?.route } ?: Overview

        Scaffold(
            topBar = {
                RallyTabRow(
                    allScreens = rallyTabRowScreens,
                    // 탭이 선택 될 떄, navigation 탐색 시작
//                    onTabSelected = { screen -> currentScreen = screen },
                    onTabSelected = { newScreen ->
                        // 이동을 하는데, 문제가 있음
                        // 1. 같은 탭을 다시 탭하면 동일한 탭이 다시 실행 됨 -> 여러개의 사본이 만들어짐  해결 - navigateSingTopTo()
                        // 2. 탭의 UI와 화면이 일치하지 않음 -> 탭 펼치기 접기가 제대로 동작하기 않음

                        navController.navigateSingleTopTo(newScreen.route)
                    },
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
                // builder: NavGraphBuilder.() -> Unit - 탐색 그래프를 정의하고 만드는 일을 담당

                // NavGraph에 3개의 컴포저블을 추가
                composable(route = Overview.route) {
                    // 하위 screen이 아닌 컴포저블을 직접 추가
                    // 이렇게 되면, RallyDestination과 화면 객체는 icon, route와 같은 탐색 관련 정보만 저장하며 Compose UI와 분리
                    OverviewScreen()
                }
                composable(route = Accounts.route) {
                    AccountsScreen()
                }
                composable(route = Bills.route) {
                    BillsScreen()
                }
            }
            Box(Modifier.padding(innerPadding)) {
                currentScreen
            }
        }
    }
}

fun NavHostController.navigateSingleTopTo(route: String) =
    this.navigate(route) {
        // 내비게이션의 옵션 중, 백스택에 사본이 최대 1개까지만 유지 될 수 있도록 설정
        launchSingleTop = true

        // 탭을 선택했을 때 백 스택에 대규모 대상 스택이 빌드되지 않도록 그래프의 시작 대상을 팝업으로 만듦
        popUpTo(
            // 그래프의 시작 대상의 id
            this@navigateSingleTopTo.graph.findStartDestination().id
        ) {
            // 상태를 저장하면 뒤로 가기 클릭 시, 백스택 전체가 "Overview"로 팝업됨
            saveState = true
        }

        // PopUpToBuilder.saveState 또는 popUpToSaveState 속성에 의해 저장된 상태를 복원하는지 여부를 결정
        // 이동할 대상 ID를 사용하여 이전에 저장된 상태가 없다면 이 옵션은 효과가 없다.
        restoreState = true
    }
