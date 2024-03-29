/*
 * Copyright (C) 2019 The Android Open Source Project
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

package com.example.android.dagger

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.example.android.dagger.di.StorageModule
import com.example.android.dagger.main.MainActivity
import com.example.android.dagger.storage.Storage
import org.junit.Rule
import org.junit.Test

// hilt에서 UI 테스트를 사용할 떄 붙이는 어노테이션
@HiltAndroidTest
// hilt 테스트 중 원래 StorageModule이아니라 TestStorageModule이 사용되어애하기 때문에
// 선언된 StorageModule을 제거
@UninstallModules(StorageModule::class)
class ApplicationTest {

    //  이 테스트 규칙은 구성요소의 상태를 관리하며 테스트에서 삽입 작업을 하는 데 사용
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Test
    fun runApp() {
        ActivityScenario.launch(MainActivity::class.java)

        // Should be in Registration/EnterDetails because the user is not registered
        onView(withText("Register to Dagger World!")).check(matches(isDisplayed()))
        onView(withId(R.id.username)).perform(typeText("username"), closeSoftKeyboard())
        onView(withId(R.id.password)).perform(typeText("password"), closeSoftKeyboard())
        onView(withId(R.id.next)).perform(click())

        // Registration/T&Cs
        onView(withText("Terms and Conditions")).check(matches(isDisplayed()))
        onView(withText("REGISTER")).perform(click())

        // Main
        onView(withText("Hello username!")).check(matches(isDisplayed()))
        onView(withText("SETTINGS")).perform(click())

        // Settings
        onView(withText("REFRESH NOTIFICATIONS")).check(matches(isDisplayed()))
        onView(withText("LOGOUT")).perform(click())

        // Login
        onView(withText("Welcome to Dagger World!")).check(matches(isDisplayed()))
        onView(withId(R.id.password)).perform(typeText("password"), closeSoftKeyboard())
        onView(withText("LOGIN")).perform(click())

        // Main
        onView(withText("Hello username!")).check(matches(isDisplayed()))
        onView(withText("SETTINGS")).perform(click())

        // Settings
        onView(withText("REFRESH NOTIFICATIONS")).check(matches(isDisplayed()))
        onView(withText("LOGOUT")).perform(click())

        // Login
        onView(withText("Welcome to Dagger World!")).check(matches(isDisplayed()))
        onView(withId(R.id.unregister)).perform(click())

        // Registration
        onView(withText("Register to Dagger World!")).check(matches(isDisplayed()))
    }
}
