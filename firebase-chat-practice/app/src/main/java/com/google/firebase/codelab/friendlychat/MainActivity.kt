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

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.AuthUI.IdpConfig.*
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.codelab.friendlychat.BuildConfig
import com.google.firebase.codelab.friendlychat.databinding.ActivityMainBinding
import com.google.firebase.codelab.friendlychat.model.FriendlyMessage
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import com.google.firebase.storage.StorageReference

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var manager: LinearLayoutManager

    private val openDocument = registerForActivityResult(MyOpenDocumentContract()) { uri ->
        uri?.let { onImageSelected(it) }
    }

    // 파이어베이스 인스턴스 변수
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase

    // 메세지 리사이클러뷰 어댑터 변수
    private lateinit var rAdapter: FriendlyMessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // This codelab uses View Binding
        // See: https://developer.android.com/topic/libraries/view-binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 파이어베이스 어스 연결 및 사용자 확인 후 확인이 안되면 페이지 진입
        auth = Firebase.auth
        if (auth.currentUser == null) {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
            return
        }

        // 파이어베이스 디비 및 파이어베이스 UI 리사이클러뷰 어댑터 초기화
        db = Firebase.database
        val messsageRef = db.reference.child(MESSAGES_CHILD)

        // 파이어베이스에 들어갈 옵션을 정의
        val option = FirebaseRecyclerOptions.Builder<FriendlyMessage>()
            .setQuery(messsageRef, FriendlyMessage::class.java)
            .build()
        rAdapter = FriendlyMessageAdapter(option, getUserName())

        with(binding) {
            progressBar.isVisible = false
            manager = LinearLayoutManager(this@MainActivity)
            manager.stackFromEnd = true
            messageRecyclerView.apply {
                layoutManager = manager
                adapter = rAdapter
            }
            // 새로운 메세지가 도착했을 떄, 스크롤을 내리기 위해 데이터를 확인하는 옵저버 등록
            rAdapter.registerAdapterDataObserver(
                MyScrollToBottomObserver(messageRecyclerView, rAdapter, manager)
            )
        }



        // Disable the send button when there's no text in the input field
        // See MyButtonObserver for details
        binding.messageEditText.addTextChangedListener(MyButtonObserver(binding.sendButton))

        // When the send button is clicked, send a text message
        // TODO: implement

        // When the image button is clicked, launch the image picker
        binding.addMessageImageView.setOnClickListener {
            openDocument.launch(arrayOf("image/*"))
        }
    }

    public override fun onStart() {
        super.onStart()
        // onStart에서도 사용자 확인 - 화면 다시 돌아왔을 떄 확인을 위함
        if (auth.currentUser == null) {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
            return
        }
    }

    // 사용자의 프로필 이미지 가져오기
    private fun getPhotoUrl(): String? = auth.currentUser?.photoUrl?.toString()
    // 사용자 이름 가져오기
    private fun getUserName(): String? = if (auth.currentUser != null) {
        auth.currentUser?.displayName
    } else {
        ANONYMOUS
    }


    // 생명주기마다 firebase db의 업데이트를 확인
    public override fun onPause() {
        rAdapter.stopListening()
        super.onPause()
    }

    public override fun onResume() {
        super.onResume()
        rAdapter.startListening()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.sign_out_menu -> {
                signOut()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun onImageSelected(uri: Uri) {
        // TODO: implement
    }

    private fun putImageInStorage(storageReference: StorageReference, uri: Uri, key: String?) {
        // Upload the image to Cloud Storage
        // TODO: implement
    }

    // 로그아웃 메소드
    private fun signOut() {
        AuthUI.getInstance().signOut(this)
        startActivity(Intent(this, SignInActivity::class.java))
        finish()
    }

    companion object {
        private const val TAG = "MainActivity"
        const val MESSAGES_CHILD = "messages"
        const val ANONYMOUS = "anonymous"
        private const val LOADING_IMAGE_URL = "https://www.google.com/images/spin-32.gif"
    }
}
