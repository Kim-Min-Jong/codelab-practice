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

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.background.workers.BlurWorker
import com.example.background.workers.CleanUpWorker
import com.example.background.workers.SaveImageToFileWorker


class BlurViewModel(application: Application) : ViewModel() {

    internal var imageUri: Uri? = null
    internal var outputUri: Uri? = null

    // WorkerManager 선언
    private val workManager = WorkManager.getInstance(application)

    // WorkInfo - LiveData를 가져와서 WorkRequest의 상태를 가져울 수 있음
    // WorkRequest의 상태를 담고 있는 세부 객체 (상태 및 데이터)
    internal val outputWorkInfos: LiveData<List<WorkInfo>>


    init {
        imageUri = getImageUri(application.applicationContext)
        // 태그를 통해 WorkInfo 라이브데이터를 가져옴
        outputWorkInfos = workManager.getWorkInfosByTagLiveData(TAG_OUTPUT)
    }

    /**
     * Create the WorkRequest to apply the blur and save the resulting image
     * @param blurLevel The amount to blur the image
     */
    internal fun applyBlur(blurLevel: Int) {
        // WorkerRequest를 생성 (두 가지 유형이 존재)
        // OneTimeWorkRequest - 한 번만 실행 할 WorkRequest
        // PeriodicWorkRequest - 일정 주기를 반복할 WorkRequest

        // 기존엔 1개의 Worker만 등록했지만 체이닝을 통해 여러개를 등록할 수 있음
//        var continuation = workManager.beginWith(OneTimeWorkRequest.from(CleanUpWorker::class.java))

        // 고유 작업 체인
        // 작업 체인을 한 번에 하나씩만 실행해야 하는 경우가 있음 (ex. 로컬 데이터와 서버와 동기화하는 작업 체인 - 첫 동기화 후 새 동기화 진행)
        // 이럴때는 beginWith 대신 beginUniqueWith를 사용하고 고유 값을 통해 작업 요청을 가능
        // 정책이 있어서 WorkManager의 처리 방식을 변경 가능
        // REPLACE - 중지 후 다시 실행
        // APPEND - 추가
        var continuation = workManager.beginUniqueWork(
            IMAGE_MANIPULATION_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            OneTimeWorkRequest.from(CleanUpWorker::class.java)
        )


        // blurLevel에 따라 추가 체이닝
        for (i in 0 until blurLevel) {
            val blurBuilder = OneTimeWorkRequestBuilder<BlurWorker>()

            if (i == 0) {
                blurBuilder.setInputData(createInputDataForUri())
            }
            continuation = continuation.then(blurBuilder.build())
        }

        // 작업 제한 조건 추가 (Constraint)
        val constraint = Constraints.Builder()
            // 기기가 충전 중일 때만 작업을 할 수 있도록 제한
            .setRequiresCharging(true)
            .build()

        val save = OneTimeWorkRequest.Builder(SaveImageToFileWorker::class.java)
            // 태그 추가를 하면서 태그를 통해 WorkInfo를 찾을 수 있게함
            .addTag(TAG_OUTPUT)
            // setConstraints 메소드를 통해 작업의 제한을 추가
            .setConstraints(constraint)
            .build()

        continuation = continuation.then(save)

        // 순서를 지키고 enqueue를 통해 등록
        continuation.enqueue()

//        // 여기선 메소드 호출시에만 실행할 것이므로 OneTime 사용
//        workManager.enqueue(blurRequest)

    }

    internal fun cancelWork() {
        // 태그를 전달해서 해당 태그 작업(고유 작업)을 취소
        workManager.cancelUniqueWork(IMAGE_MANIPULATION_WORK_NAME)
    }

    // WorkManager를 통한 데이터 교환을 위한 메소드
    private fun createInputDataForUri(): Data {
        // Data Builder 객체 생성
        val builder = Data.Builder()
        // imageUri에 대한 Data를 생성
        imageUri?.let {
            builder.putString(KEY_IMAGE_URI, it.toString())
        }
        // Data 객체를 반환
        return builder.build()
    }

    private fun uriOrNull(uriString: String?): Uri? {
        return if (!uriString.isNullOrEmpty()) {
            Uri.parse(uriString)
        } else {
            null
        }
    }

    private fun getImageUri(context: Context): Uri {
        val resources = context.resources

        val imageUri = Uri.Builder()
            .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
            .authority(resources.getResourcePackageName(R.drawable.android_cupcake))
            .appendPath(resources.getResourceTypeName(R.drawable.android_cupcake))
            .appendPath(resources.getResourceEntryName(R.drawable.android_cupcake))
            .build()

        return imageUri
    }

    internal fun setOutputUri(outputImageUri: String?) {
        outputUri = uriOrNull(outputImageUri)
    }

    class BlurViewModelFactory(private val application: Application) : ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(BlurViewModel::class.java)) {
                BlurViewModel(application) as T
            } else {
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}
