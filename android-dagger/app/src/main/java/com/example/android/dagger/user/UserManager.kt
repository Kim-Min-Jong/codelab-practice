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

package com.example.android.dagger.user

import com.example.android.dagger.di.UserComponent
import com.example.android.dagger.storage.Storage
import javax.inject.Inject
import javax.inject.Singleton

private const val REGISTERED_USER = "registered_user"
private const val PASSWORD_SUFFIX = "password"

/**
 * Handles User lifecycle. Manages registrations, logs in and logs out.
 * Knows when the user is logged in.
 */

// @Inject를 달아주면, dagger는 RegisterViewModel과 UserManager의 인스턴스 제공방법을 다 알게 됨
@Singleton
class UserManager @Inject constructor(
    private val storage: Storage,
    // UserManager는 User 정보를 사용하기에 UserComponent의 수명주기를 따라야해
    // UserComponent 인스턴스가 필요하다.
    private val userComponentFactory: UserComponent.Factory
    ) {

    /**
     *  UserDataRepository is specific to a logged in user. This determines if the user
     *  is logged in or not, when the user logs in, a new instance will be created.
     *  When the user logs out, this will be null.
     */
    // UserComponent를 주입 받으므로 제거 가능
//    var userDataRepository: UserDataRepository? = null

    // user 객체를 component를 이용해 생성
    var userComponent: UserComponent? = null
        private set


    val username: String
        get() = storage.getString(REGISTERED_USER)

    fun isUserLoggedIn() = userComponent!= null

    fun isUserRegistered() = storage.getString(REGISTERED_USER).isNotEmpty()

    fun registerUser(username: String, password: String) {
        storage.setString(REGISTERED_USER, username)
        storage.setString("$username$PASSWORD_SUFFIX", password)
        userJustLoggedIn()
    }

    fun loginUser(username: String, password: String): Boolean {
        val registeredUser = this.username
        if (registeredUser != username) return false

        val registeredPassword = storage.getString("$username$PASSWORD_SUFFIX")
        if (registeredPassword != password) return false

        userJustLoggedIn()
        return true
    }

    fun logout() {
        userComponent = null
    }

    fun unregister() {
        val username = storage.getString(REGISTERED_USER)
        storage.setString(REGISTERED_USER, "")
        storage.setString("$username$PASSWORD_SUFFIX", "")
        logout()
    }

    // user component 생성
    private fun userJustLoggedIn() {
        userComponent = userComponentFactory.create()
    }
}
