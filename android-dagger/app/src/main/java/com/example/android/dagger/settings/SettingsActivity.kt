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

package com.example.android.dagger.settings

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import com.example.android.dagger.MyApplication
import com.example.android.dagger.R
import com.example.android.dagger.login.LoginActivity
import javax.inject.Inject

// Setting Activity 도 dagger로 전환

class SettingsActivity : AppCompatActivity() {

    @Inject
    lateinit var settingsViewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        // super.onCreate 전 액티비티를 주입
        (application as MyApplication).appComponent.inject(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // dagger로 주입되기 때문에 삭제
//        val userManager = (application as MyApplication).userManager
//        settingsViewModel = SettingsViewModel(userManager.userDataRepository!!, userManager)
        setupViews()
    }

    private fun setupViews() {
        findViewById<Button>(R.id.refresh).setOnClickListener {
            settingsViewModel.refreshNotifications()
        }
        findViewById<Button>(R.id.logout).setOnClickListener {
            settingsViewModel.logout()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or
                    Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }
}
