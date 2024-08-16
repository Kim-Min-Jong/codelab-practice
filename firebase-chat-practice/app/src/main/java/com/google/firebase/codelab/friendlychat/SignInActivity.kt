/**
 * Copyright Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.firebase.codelab.friendlychat

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.IdpResponse
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.codelab.friendlychat.databinding.ActivitySignInBinding

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding

    // ActivityResultLauncher
    // 파이어베이스 로그인 작업후 정보를 가져올 액티비티 런처
    private val signInLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(FirebaseAuthUIActivityResultContract(), this::onSignInResult)

    // 파이어베이스 인스턴스
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // This codelab uses View Binding
        // See: https://developer.android.com/topic/libraries/view-binding
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // auth 초기화
        auth = Firebase.auth
    }

    public override fun onStart() {
        super.onStart()
        // 로그인 된 유저가 없을 때
        // firebase UI로 로그인 시작
        if (Firebase.auth.currentUser == null) {
            // FirebaseUi를 통한 파이어베이스 로그인
            val signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setLogo(R.mipmap.ic_launcher)
                .setAvailableProviders(
                    listOf(
                        // Ui에 이메일 배너 추가
                        AuthUI.IdpConfig.EmailBuilder().build(),
                        // UI에 구글 로그인 배너 추가
                        AuthUI.IdpConfig.GoogleBuilder().build()
                    )
                )
                .build()

            signInLauncher.launch(signInIntent)
        } else {
            goToMainActivity()
        }
    }

    private fun signIn() {
        // TODO: implement
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        // 로그인 결과 처리
        if (result.resultCode == RESULT_OK) {
            // 성공시 메인 화면으로
            goToMainActivity()
        } else {
            // 실패시 토스트 메세지
            Toast.makeText(
                this,
                "There was an error signing in",
                Toast.LENGTH_LONG).show()

            val response = result.idpResponse
            if (response == null) {
                Log.w(TAG, "Sign in canceled")
            } else {
                Log.w(TAG, "Sign in error", response.error)
            }
        }
    }

    private fun goToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    companion object {
        private const val TAG = "SignInActivity"
    }
}
