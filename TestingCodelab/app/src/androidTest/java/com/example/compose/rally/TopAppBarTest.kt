package com.example.compose.rally

import androidx.compose.ui.test.assertIsSelected
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

        composeTestRule
            // 탭의 문자를 대문자로 표시
            .onNodeWithContentDescription(RallyScreen.Accounts.name.uppercase())
            // 대문자로 존재하는지 확인
            .assertExists()
    }
}
