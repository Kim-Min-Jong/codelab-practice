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

package com.example.android.dagger.registration

import com.example.android.dagger.di.ActivityScope
import com.example.android.dagger.user.UserManager
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

/**
 * RegistrationViewModel is the ViewModel that the Registration flow ([RegistrationActivity]
 * and fragments) uses to keep user's input data.
 */

// 뷰모델 스코프 지정 (hilt에서 제공하는 ActivityScoped로 변경가능 - 동일)
@ActivityScoped

// @Inject는 dagger에게 해당 타입의 인스턴스(생성자 매개변수)를 제공하는 방법을 알린다.
// 여기서는 RegistrationViewModel의 생성자가 UserManager를 인수로 사용하므로 종속성으로 관리되어 dagger에게 알려진다.

// 여기까지만 하면 dagger는 UserManager의 생성 방법을 모르므로, UserManager 또한 @Inject를 통해 종속성을 부여해야한다.
class RegistrationViewModel @Inject constructor(val userManager: UserManager) {

    private var username: String? = null
    private var password: String? = null
    private var acceptedTCs: Boolean? = null

    fun updateUserData(username: String, password: String) {
        this.username = username
        this.password = password
    }

    fun acceptTCs() {
        acceptedTCs = true
    }

    fun registerUser() {
        assert(username != null)
        assert(password != null)
        assert(acceptedTCs == true)

        userManager.registerUser(username!!, password!!)
    }
}
