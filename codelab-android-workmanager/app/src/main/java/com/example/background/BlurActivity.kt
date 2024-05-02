/*
 * Copyright (C) 2018 The Android Open Source Project
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

package com.example.background

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.background.databinding.ActivityBlurBinding

class BlurActivity : AppCompatActivity() {

    private val viewModel: BlurViewModel by viewModels {
        BlurViewModel.BlurViewModelFactory(
            application
        )
    }
    private lateinit var binding: ActivityBlurBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBlurBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.goButton.setOnClickListener { viewModel.applyBlur(blurLevel) }

        // 블러 처리된 이미지 파일 보는 버튼 이벤트 리스너
        binding.seeFileButton.setOnClickListener {
            viewModel.outputUri?.let { currentUri ->
                // 이미지 파일을 보여줄 Intent 생성 (브라우저)
                val actionView = Intent(Intent.ACTION_VIEW, currentUri)
                actionView.resolveActivity(packageManager)?.run {
                    startActivity(actionView)
                }
            }
        }

        // WorkInfo 상태를 보고 UI조작
        viewModel.outputWorkInfos.observe(this) { listOfWorkInfo ->
            if (listOfWorkInfo.isNullOrEmpty()) return@observe

            // WorkInfo를 가져옴
            val workInfo = listOfWorkInfo[2]
            // 상태를 확인하고 그에 따라 Ui를 조작
            if (workInfo.state.isFinished) {
                showWorkFinished()

                // 이미지파일을 가져오기
                val outputImageUri = workInfo.outputData.getString(KEY_IMAGE_URI)

                // URI가 있다면 올바르게 저장된 거시니 츨력 버튼을 표시하고 setOutputUri를 호출
                if (outputImageUri.isNullOrEmpty().not()) {
                    viewModel.setOutputUri(outputImageUri)
                    binding.seeFileButton.isVisible = true
                } else {
                    showWorkInProgress()
                }

            } else {
                showWorkInProgress()
            }
        }
    }

    /**
     * Shows and hides views for when the Activity is processing an image
     */
    private fun showWorkInProgress() {
        with(binding) {
            progressBar.visibility = View.VISIBLE
            cancelButton.visibility = View.VISIBLE
            goButton.visibility = View.GONE
            seeFileButton.visibility = View.GONE
        }
    }

    /**
     * Shows and hides views for when the Activity is done processing an image
     */
    private fun showWorkFinished() {
        with(binding) {
            progressBar.visibility = View.GONE
            cancelButton.visibility = View.GONE
            goButton.visibility = View.VISIBLE
        }
    }

    private val blurLevel: Int
        get() =
            when (binding.radioBlurGroup.checkedRadioButtonId) {
                R.id.radio_blur_lv_1 -> 1
                R.id.radio_blur_lv_2 -> 2
                R.id.radio_blur_lv_3 -> 3
                else -> 1
            }
}
