package com.example.compose.rally

import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasParent
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import com.example.compose.rally.ui.components.RallyTopAppBar
import org.junit.Rule
import org.junit.Test

class TopAppBarTest {

    // 컴포즈 테스트 룰을 세팅
    // 테스트 중인 컴포즈 컨텐츠를 설정하고 상호작용 가능하도록
    @get:Rule
    val composeTestRule = createComposeRule()

    // 테스트 메소드
    @Test
    fun rallyTopBarTest() {
        val allScreens = RallyScreen.entries

        // 룰을 통해 compose 함수 사용가능
        composeTestRule.setContent {
            RallyTopAppBar(
                allScreens = allScreens,
                onTabSelected = {},
                currentScreen = RallyScreen.Accounts
            )
        }

        composeTestRule
            // Content Description이 있는 UI 노드를 찾기
            .onNodeWithContentDescription(RallyScreen.Accounts.name)
            // 선택되었는지 테스트
            .assertIsSelected()
    }


    @Test
    fun rallyTopBarTest_currentLabelExists() {
        val allScreens = RallyScreen.entries

        // 룰을 통해 compose 함수 사용가능
        composeTestRule.setContent {
            RallyTopAppBar(
                allScreens = allScreens,
                onTabSelected = {},
                currentScreen = RallyScreen.Accounts
            )
        }

        // onNode를 통해 각 종 노드를 탐색하여 테스트 할 수 있음
        composeTestRule
            //
            .onNode(
                // 텍스트 요소가 있는지
                hasText(RallyScreen.Accounts.name.uppercase())
                        and
                        // 부모 요소가 있는지
                        hasParent(
                            hasContentDescription(RallyScreen.Accounts.name)
                        ),
                // 병합되지 않은 노드 트리만을 탐색
                useUnmergedTree = true
            )
            // 대문자로 존재하는지 확인
            .assertExists()
    }
}
