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

package com.example.android.dagger.main

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.android.dagger.MyApplication
import com.example.android.dagger.R
import com.example.android.dagger.login.LoginActivity
import com.example.android.dagger.registration.RegistrationActivity
import com.example.android.dagger.settings.SettingsActivity
import com.example.android.dagger.user.UserManager
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    // UserManager는 RegistrationComponent, LoginComponent와 달리 UserManager는 MainActivity 및 SettingsActivity 모두에서 사용됨
    // EntryPoint 인터페이스는 한 번만 생성하면 됨
    // 이 EntryPoint는 MainActivity 및 SettingsActivity 모두에서 사용 가능
    // 그래서 간단히 하기위해 MainActivity에서 인터페이스 선언
    @InstallIn(ApplicationComponent::class)
    @EntryPoint
    interface UserManagerEntryPoint {
        fun userManager(): UserManager
    }

    // 주입 시 주의
    // dagger inject 변수는 패키지 단 가시성(접근자)이 필요하므로 private 키워드 금지


    // dagger - UserComponent에 의해 주입되므로 제거 가능
//    // UserManager와 연결을 위해 주입
//    @Inject
//    lateinit var userManager: UserManager

    // MainViewModel과 연결을 위해 주입
    @Inject
    lateinit var mainViewModel: MainViewModel

    /**
     * If the User is not registered, RegistrationActivity will be launched,
     * If the User is not logged in, LoginActivity will be launched,
     * else carry on with MainActivity
     */
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        // MainActivity를 dagger에 알림
//        val userManager = (application as MyApplication).appComponent.userManager()
        val entryPoint = EntryPointAccessors.fromApplication(applicationContext, UserManagerEntryPoint::class.java)
        val userManager = entryPoint.userManager()

        // 수동 주입은 더 이상 필요하지 않음 - dagger가 알아서 해줌
//        val userManager = (application as MyApplication).userManager
        if (!userManager.isUserLoggedIn()) {
            if (!userManager.isUserRegistered()) {
                startActivity(Intent(this, RegistrationActivity::class.java))
                finish()
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        } else {
            setContentView(R.layout.activity_main)
            // 수동 주입은 더 이상 필요하지 않음 - dagger가 알아서 해줌
//            mainViewModel = MainViewModel(userManager.userDataRepository!!)
            // UserComponet - UserManger를 통해 주입
            userManager.userComponent!!.inject(this)
            setupViews()
        }
    }

    /**
     * Updating unread notifications onResume because they can get updated on SettingsActivity
     */
    override fun onResume() {
        super.onResume()
        findViewById<TextView>(R.id.notifications).text = mainViewModel.notificationsText
    }

    private fun setupViews() {
        findViewById<TextView>(R.id.hello).text = mainViewModel.welcomeText
        findViewById<Button>(R.id.settings).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }
}
