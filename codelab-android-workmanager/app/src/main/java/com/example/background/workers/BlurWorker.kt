package com.example.background.workers

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.background.R

/*
    알아야 하는 WorkManager class 기본 사항

    1. Worker - 백그라운드에서 실행하고자 하는 실제 작업의 코드를 입력, 이 클래스를 상속 받고 doWork() 메소드를 재정의해서 사용
    2. WorkRequest - 작업 실행을 요청, WorkRequest를 만드는 과정에서 Worker를 전달해야함, WorkRequest를 만들 때 Worker를 실행할 시점에 적용되는 Constraint를 지정 할 수도 있ㅇ듬
    3. WorkerManager - 실제로 WorkRequest를 예약하고 실행, 지정된 제약을 준수하면서 시스템 리소스에 부하를 분산하는 방식으로 WorkRequest를 예약

 */

// 이미지 블러 처리를 요청하는 BlurWorker 클래스
class BlurWorker(
    context: Context,
    params: WorkerParameters
): Worker(context, params) /* Worker를 상속해서 Worker 클래스 생성*/ {

    // Worker가 실제 실행하는 메소드 - 작업을 정의
    override fun doWork(): Result {
        val appContext = applicationContext

        // 이미지 블러 처리에 관해 알리는 Notification 실행 (사용자 알림)
        makeStatusNotification("Blurring image", appContext)

        return try {
            // 블러 처리할 사진
            val picture = BitmapFactory.decodeResource(
                appContext.resources,
                R.drawable.android_cupcake
            )

            // 블러 처리
            val blurBitmap = blurBitmap(picture, appContext)

            // 블러 처리한 비트맵을 임시 파일에 씀 (URI 생성)
            val outputUri = writeBitmapToFile(appContext, blurBitmap)

            // 블러 처리가 완료되었다는 Notification 실행 (URI 노출)
            makeStatusNotification("Output is $outputUri", appContext)

            // 성공 처리를 반환
            Result.success()
        } catch (t: Throwable) {
            Log.e(TAG, "Error applying blur")
            // 실패시 실패 처리를 반환
            Result.failure()
        }
    }

    companion object {
        private const val TAG = "BlurWorker"
    }
}
