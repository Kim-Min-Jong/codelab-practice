package com.pr.camerax

import android.os.Build
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.ImageCapture
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.pr.camerax.databinding.ActivityMainBinding
import java.util.concurrent.ExecutorService

typealias LumaListener = (luma: Double) -> Unit

class MainActivity : AppCompatActivity() {
    // 뷰바인딩
    private lateinit var binding: ActivityMainBinding

    // 사진 캡쳐
    private var imageCapture: ImageCapture? = null

    // 영상 캡쳐
    private var videoCapture: VideoCapture<Recorder>? = null

    // 녹화할 변수
    private var recording: Recording? = null

    // 카메라 실행 서비스
    private lateinit var cameraExecutor: ExecutorService
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 카메라 권한을 요청
        if (allPermissionGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

    }

    // all function을 통해 모든 권한을 확인
    private fun allPermissionGranted(): Boolean = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext,
            it
        ) == PackageManager.PERMISSION_GRANTED
    }


    // TODO
    private fun takePhoto() {}

    private fun captureVideo() {}

    private fun startCamera() {}

    companion object {
        private const val TAG = "CameraXApp"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf (
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }

}
