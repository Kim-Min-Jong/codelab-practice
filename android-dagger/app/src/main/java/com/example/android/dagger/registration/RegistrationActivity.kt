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

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.android.dagger.MyApplication
import com.example.android.dagger.R
import com.example.android.dagger.main.MainActivity
import com.example.android.dagger.registration.enterdetails.EnterDetailsFragment
import com.example.android.dagger.registration.termsandconditions.TermsAndConditionsFragment
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Inject

// AndroidEntryPoint를 통해 진입점을 재설정
// 이제 직접 만든 EntryPoint는 필요없음 -- AppComponent도 삭제 가능
@AndroidEntryPoint
class RegistrationActivity : AppCompatActivity() {

    // hilt로 이전하는 동안 hilt의 entrypoint를 통해 dagger의 종속 항목에 요청할 수 있다.
    // EntryPoint를 사용하면 모든 dagger 컴포넌트를 이전하는동안 계속 앱이 작동 할 수 있도록 한다.
    // 각 dagger 컴포넌트를 hilt에서 생성된 ApplicationComponent의 종속항목으로 교체한다.
//    @InstallIn(ApplicationComponent::class)
//    @EntryPoint
//    interface  RegistrationEntryPoint {
//        fun registrationComponent(): RegistrationComponent.Factory
//    }

    lateinit var registrationComponent: RegistrationComponent

    // RegisterViewModel에 Inject를 달아 줌으로서 dagger에 제공
    // 인스턴스화가 불가능한 ViewModel을 dagger에 전달하기 위해 activity단 주입해 종속 그래프에서 찾도록함
    @Inject
    lateinit var registrationViewModel: RegistrationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        // 중요!
        // 액티비티에서 사용할 때, dagger 주입은 프래그먼트 복원 문제 때문에 super.onCreate 이전에서 해야한다.
        // super.onCreate에서는 액티비티 복원단계에서 액티비티 바인딩에 프래그먼트가 붙기 때문에

        // dagger에게 이 activity를 알린다.
//        (application as MyApplication).appComponent.inject(this)

        // regisrartion activity scope에서만 동작하도록 알린다.

        // dagger로 된 부분을 hilt로 교체한다 (EntryPoint 사용)
//        registrationComponent = (application as MyApplication).appComponent.registrationComponent().create()
//        registrationComponent.inject(this)
//        val entryPoint = EntryPointAccessors.fromApplication(applicationContext, RegistrationEntryPoint::class.java)
//        registrationComponent = entryPoint.registrationComponent().create()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        // @Inject로 제공 했으므로 다시 초기화는 할 필요가 없음
        // dagger가 알아서 해줌
//        registrationViewModel = RegistrationViewModel((application as MyApplication).userManager)
        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_holder, EnterDetailsFragment())
            .commit()
    }

    /**
     * Callback from EnterDetailsFragment when username and password has been entered
     */
    fun onDetailsEntered() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_holder, TermsAndConditionsFragment())
            .addToBackStack(TermsAndConditionsFragment::class.java.simpleName)
            .commit()
    }

    /**
     * Callback from T&CsFragment when TCs have been accepted
     */
    fun onTermsAndConditionsAccepted() {
        registrationViewModel.registerUser()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }
}
