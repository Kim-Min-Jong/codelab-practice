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

import android.app.Application
import com.example.android.dagger.di.AppComponent
import com.example.android.dagger.di.DaggerAppComponent
import com.example.android.dagger.storage.SharedPreferencesStorage
import com.example.android.dagger.user.UserManager

open class MyApplication : Application() {

    // android에서는 앱이 실행되는 동안 그래프가 메모리에 있는 것을 원하기에
    // application 클래스에서 dagger 그래프를 만든다.
    // 이 방식은 앱의 라이프사이클에 연결된다.
    // application 단에서 그래프를 연결하면 context도 사용할 수 있으며
    // 사용자 정의 application을 사용하므로 테스트 및 활용성도 좋아진다.
    val appComponent: AppComponent by lazy {
        // dagger는 DaggerAppComponent에 의해 생성된다.
        initializeComponent()
    }

    open fun initializeComponent(): AppComponent {
        return DaggerAppComponent.factory().create(applicationContext)
    }

    // UserManager도 dagger에 의해 관리 될것이기에 제거
//    open val userManager by lazy {
//        UserManager(SharedPreferencesStorage(this))
//    }
}
