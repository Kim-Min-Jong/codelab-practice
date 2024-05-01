package com.example.background.workers

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

// 이미지를 실제 영구파일로 저장하는 Worker
// 작업 체인을 활용
class SaveImageToFileWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {
    override fun doWork(): Result {

    }

}
