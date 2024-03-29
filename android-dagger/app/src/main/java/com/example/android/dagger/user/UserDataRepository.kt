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

import javax.inject.Inject
import kotlin.random.Random

/**
 * UserDataRepository contains user-specific data such as username and unread notifications.
 */
// MainViewModel에서 Inject를 통해 알렸으니 repository도 dagger에 주입

// 이제 MainActivity와 SettingsActivity 두 군데서 사용되기 떄문에 인스턴스 접근? 생성 문제가 생김
// 앞서 봤던 것 중 @Singleton을 적용할 수도 있지만
// user flow에서 login out / register는 다른 flow이기 때문에 같은 인스턴스를 사용하지 않길 원함
// 그래서 UserData도 SubComponent로 빼는 작업이 필요함

//@LoggedUserScope (hilt에서 관리되므로 삭제)
class UserDataRepository @Inject constructor(
    // 이미 hilt에 의해 삽입되있으므로 순환 참조 방지를 위해 삭제
//    private val userManager: UserManager
) {

    var username: String? = null
        private set

    var unreadNotifications: Int? = null
        private set
    init {
        unreadNotifications = randomInt()
    }

    fun refreshUnreadNotifications() {
        unreadNotifications = randomInt()
    }

    fun initData(username: String) {
        this.username = username
        unreadNotifications = randomInt()
    }

    fun cleanUp() {
        username = null
        unreadNotifications = -1
    }
}

fun randomInt(): Int {
    return Random.nextInt(until = 100)
}
