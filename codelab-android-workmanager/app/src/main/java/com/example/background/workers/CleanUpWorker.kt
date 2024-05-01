package com.example.background.workers

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.background.OUTPUT_PATH
import java.io.File

// 임시파일을 정리하는 Worker
// 작업 체인을 활용
class CleanUpWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {
    // 삭제만 하니 입력 및 출력을 받을 필요가 없음 (Data 객체 필요 X)
    override fun doWork(): Result {
        // 시작 Notification 발행
        makeStatusNotification("Cleaning up old temporary files", applicationContext)
        sleep()

        return try {
            // 저장될 경로를 가진 파일
            val outputDirectory = File(applicationContext.filesDir, OUTPUT_PATH)

            // 저장된 경로가 있을 때만 삭제 실행
            if (outputDirectory.exists()) {
                // 경로의 파일들을 가져옴
                val entries = outputDirectory.listFiles()
                // 파일이 있으면
                if (entries != null) {
                    // 순회하면서
                    for (entry in entries) {
                        val name = entry.name
                        // png형식의 이름을 가진 파일들을
                        if (name.isNotEmpty() && name.endsWith(".png")) {
                            // 삭제
                            val deleted = entry.delete()
                            // 로깅
                            Log.i(TAG, "Deleted $name - $deleted")
                        }
                    }
                }
            }
            // 성공 반환
            Result.success()
        } catch (exception: Exception) {
            exception.printStackTrace()
            Result.failure()
        }
    }

    companion object {
        private const val TAG = "CleanupWorker"
    }
}
