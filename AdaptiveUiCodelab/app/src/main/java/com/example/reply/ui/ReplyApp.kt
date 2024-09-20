/*
 * Copyright 2022 Google LLC
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

package com.example.reply.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowSize
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.example.reply.data.Email

@Composable
fun ReplyApp(
    replyHomeUIState: ReplyHomeUIState,
    onEmailClick: (Email) -> Unit,
) {
    ReplyNavigationWrapperUI {
        ReplyAppContent(
            replyHomeUIState = replyHomeUIState,
            onEmailClick = onEmailClick
        )
    }
}

@Composable
private fun ReplyNavigationWrapperUI(
    content: @Composable () -> Unit = {}
) {
    var selectedDestination: ReplyDestination by remember {
        mutableStateOf(ReplyDestination.Inbox)
    }

    val windowSize = with(LocalDensity.current) {
        currentWindowSize().toSize().toDpSize()
    }

    // 원도우 사이즈에 따른 타입 분기
    val layoutType = if (windowSize.width >= 1200.dp) {
        // 1200dp 이상이면 Drawer방식
        NavigationSuiteType.NavigationDrawer
    } else {
        // 그 외는 계산해서 적응형으로
        NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(
            currentWindowAdaptiveInfo()
        )
    }

    // 적응형 위젯 추가
    NavigationSuiteScaffold(
        // 윈도우 타입 설정
        layoutType = layoutType,
        // 네비게이션 아이템
        // 윈도우 사이즈에따라 위치가 바뀜ㅁ
        navigationSuiteItems =  {
            ReplyDestination.entries.forEach {
                item(
                    selected = it == selectedDestination,
                    onClick = {},
                    icon = {
                        Icon(
                            imageVector = it.icon,
                            contentDescription = stringResource(it.labelRes)
                        )
                    },
                    label = {
                        Text(text = stringResource(it.labelRes))
                    }
                )
            }
        }
    ) {
        // 네비게이션 컨텐츠
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.inverseOnSurface)
        ) {
            Box(modifier = Modifier.weight(1f)) {
                content()
            }

            NavigationBar(modifier = Modifier.fillMaxWidth()) {
                ReplyDestination.entries.forEach {
                    NavigationBarItem(
                        selected = it == selectedDestination,
                        onClick = { /*TODO update selection*/ },
                        icon = {
                            Icon(
                                imageVector = it.icon,
                                contentDescription = stringResource(it.labelRes)
                            )
                        },
                        label = {
                            Text(text = stringResource(it.labelRes))
                        },
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun ReplyAppContent(
    replyHomeUIState: ReplyHomeUIState,
    onEmailClick: (Email) -> Unit,
) {
    // 표시할 창과 이 창에 표시되어야 하는 컨텐츠를 제어하는 탐색기
    val navigator = rememberListDetailPaneScaffoldNavigator<Long>()

    // 윈도우 사이즈에 따라 영역을 두개로 나누는 scaffold
    ListDetailPaneScaffold(
        directive = navigator.scaffoldDirective,
        value = navigator.scaffoldValue,
        // 리스트 형식
        listPane = {
            ReplyListPane(
                replyHomeUIState = replyHomeUIState,
                onEmailClick = { email ->
                    onEmailClick(email)
                    // 두 창이 모두 표시될 때 잘 작동하지만 창에 창 하나만 표시할 공간이 있는 경우 항목을 탭해도 아무 일도 발생하지 않는 것처럼 보임
                    // 선택한 이메일이 업데이트되더라도 ListDetailPaneScaffold가 이러한 구성의 목록 창에 포커스를 유지하기 때문
                    // 이를 수정하기 위해 디테일로 가는 탐색명령을 명시
                    navigator.navigateTo(ListDetailPaneScaffoldRole.Detail, email.id)
                }
            )
        },
        // 자세하 형식
        detailPane = {
            if (replyHomeUIState.selectedEmail != null) {
                ReplyDetailPane(replyHomeUIState.selectedEmail)
            }
        }
    )

    // You will implement an adaptive two-pane layout here.

}
