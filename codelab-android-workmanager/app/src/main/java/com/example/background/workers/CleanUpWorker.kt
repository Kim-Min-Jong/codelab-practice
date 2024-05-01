package com.example.background.workers

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

// 임시파일을 정리하는 Worker
// 작업 체인을 활용
class CleanUpWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {
    override fun doWork(): Result {

    }

}
