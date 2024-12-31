package com.pr.foldablepractice

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.PositionAssertions
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.window.layout.FoldingFeature
import androidx.window.layout.FoldingFeature.Orientation.Companion.VERTICAL
import androidx.window.layout.FoldingFeature.State.Companion.FLAT
import androidx.window.testing.layout.FoldingFeature
import androidx.window.testing.layout.TestWindowLayoutInfo
import androidx.window.testing.layout.WindowLayoutInfoPublisherRule

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Rule
import org.junit.rules.RuleChain
import org.junit.rules.TestRule

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    private val activityRule = ActivityScenarioRule(MainActivity::class.java)
    // WindowLayoutInfo 값의 스트림 소비를 테스트하는 데 도움이 되는 규칙
    // WindowLayoutInfoPublisherRule을 사용하면 필요에 따라 다양한 WindowLayoutInfo 값을 푸시할 수 있음
    private val publisherRule = WindowLayoutInfoPublisherRule()

    @get:Rule
    val testRule: TestRule

    init {
        // 기본 룰을 룰 체이닝을 통해 생성
        testRule = RuleChain.outerRule(publisherRule).around(activityRule)
    }

    // MainActivity에서 TextView는 접기 기능의 왼쪽에 정렬됨.
    // 올바르게 구현되었는지 확인하는 테스트
    @Test
    fun testText_is_left_of_Vertical_FoldingFeature() {
       // 액티비티에서의 시나리오
        activityRule.scenario.onActivity { activity ->
            // 테스트용 foldingFeature
           val hinge = FoldingFeature(
               activity = activity,
               state = FLAT,
               orientation = VERTICAL,
               size = 2
           )
            // 테스트용 객체를 통해 테스트용 Layout Info를 생성
            val expected = TestWindowLayoutInfo(listOf(hinge))
            publisherRule.overrideWindowLayoutInfo(expected)
       }
       // UI 요소가 FoldingFeature를 피하여 정렬되어야 하는 위치에 있는지 테스트
       onView(withId(R.id.layout_change)).check(
           PositionAssertions.isCompletelyLeftOf(withId(R.id.folding_feature))
       )
    }
}
