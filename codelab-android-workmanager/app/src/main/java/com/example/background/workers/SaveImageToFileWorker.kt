package com.example.background.workers

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.background.KEY_IMAGE_URI
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// 이미지를 실제 영구파일로 저장하는 Worker
// 작업 체인을 활용
class SaveImageToFileWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    private val title = "Blurred Image"
    private val dateFormatter = SimpleDateFormat(
        "yyyy.MM.dd 'at' HH:mm:ss z",
        Locale.getDefault()
    )
    override fun doWork(): Result {
        // 시작 Notification 발행
        makeStatusNotification("Cleaning up old temporary files", applicationContext)
        sleep()

        // 컨텐트 프로바이더 - 시스템의 각종 설정값이나 DB 에 접근하게 해 줌
        // 저장소 저장을 위한 리졸버 선언 (결과를 반환하는 브릿지 역할)
        val resolver = applicationContext.contentResolver
        return try {
            // 이미지 URI 선언
            val resourceUri = inputData.getString(KEY_IMAGE_URI)
            val bitmap = BitmapFactory.decodeStream(
                resolver.openInputStream(Uri.parse(resourceUri))
            )
            // 내부 저장소에 저장후 URL 반환
            val imageUrl = MediaStore.Images.Media.insertImage(
                resolver,
                bitmap,
                title,
                dateFormatter.format(Date())
            )

            if (imageUrl.isNullOrEmpty().not()) {
                val output = workDataOf(KEY_IMAGE_URI to imageUrl)
                // 성공 반환 시 WorkerManager에 output도 함께 반환
                Result.success(output)
            } else {
                Log.e(TAG, "Writing to MediaStore failed")
                Result.failure()
            }

        } catch (exception: Exception) {
            exception.printStackTrace()
            Result.failure()
        }

    }
    companion object {
        private const val TAG = "SaveImageToFileWorker"
    }
}
