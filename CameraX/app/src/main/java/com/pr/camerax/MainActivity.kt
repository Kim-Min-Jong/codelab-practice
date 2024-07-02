package com.pr.camerax

import android.os.Build
import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import com.pr.camerax.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.Locale
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

        // 사진, 동영상 버튼 리스너 등록
        with(binding) {
            imageCaptureButton.setOnClickListener { takePhoto() }
            videoCaptureButton.setOnClickListener { captureVideo() }
        }
    }

    // all function을 통해 모든 권한을 확인
    private fun allPermissionGranted(): Boolean = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext,
            it
        ) == PackageManager.PERMISSION_GRANTED
    }

    // 권한 확인 후 콜백
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // 요청 코드가 올바른지 확인
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            // 그 후 ,모든 권한이 부여되었는지 학인
            if (allPermissionGranted()) {
                startCamera()
            } else {
                // 안되있다면 토스트 메세지
                Toast.makeText(
                    this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }


    // TODO
    private fun takePhoto() {
        // imageCapture 객체
        // 객체가 null이면 자동 종료 되도록
        val imageCapture = imageCapture ?: return

        // 사진 정보를 담을 MediaStore 생성
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.KOREA)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
            }
        }

        // 출력될 파일 및 메타데이터의 옵션을 생성
        val outputOptions = ImageCapture.OutputFileOptions.Builder(
            contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        ).build()

        // 이미지 (사진) 캡처 리스너 등록
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            // 이미지가 저장될 떄의 콜백
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val msg = "Photo capture succeeded: ${outputFileResults.savedUri}"
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, msg)
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exception.message}", exception)
                }
            }
        )
    }

    private fun captureVideo() {
        // 비디오 캡처 객체 생성 없으면 종료
        val videoCapture = this.videoCapture ?: return
        binding.videoCaptureButton.isEnabled = false

        // 지금 레코딩 중인지 (객체가 있는지)
        val curRecording = recording
        // 녹화중인 활성 녹화가 있다면
        if (curRecording != null) {
            // 모두 해제하고 종료
            curRecording.stop()
            recording = null
            return
        }

        // 녹화 파일, 메타데이터 정보 생성
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/CameraX-Video")
            }
        }

        // 출력될 파일 및 메타데이터의 옵션을 생성
        val mediaStoreOutputOptions = MediaStoreOutputOptions.Builder(
            contentResolver,
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        )
            .setContentValues(contentValues)
            .build()

        // 레코딩 객체 초기화
        // 출력 옵션을 VideoCapture<Recorder>로 구성하고 오디오 녹음을 서정
        recording = videoCapture.output.prepareRecording(
            this,
            mediaStoreOutputOptions
        ).apply {
            if (PermissionChecker.checkSelfPermission(this@MainActivity,
                    Manifest.permission.RECORD_AUDIO) ==
                PermissionChecker.PERMISSION_GRANTED)
            {
                // 오디오 설정
                withAudioEnabled()
            }

        }
            // 새 녹음을 시작하고 record event를 등록
            .start(ContextCompat.getMainExecutor(this)) { recordEvent ->
            with (binding) {
                when(recordEvent) {
                    // 카메라에서 요청 녹화를 시작 할 때
                    is VideoRecordEvent.Start -> {
                        videoCaptureButton.apply {
                            text = getString(R.string.stop_capture)
                            isEnabled = true
                        }
                    }
                    // 활성 녹화가 완료 될 때
                    is VideoRecordEvent.Finalize -> {
                        // 에러가 없다면
                        if (!recordEvent.hasError()) {
                            // 정상 메세지 출력
                            val msg = "Video capture succeeded: " +
                                    "${recordEvent.outputResults.outputUri}"
                            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT)
                                .show()
                            Log.d(TAG, msg)
                        } else {
                            // 오류가 있을 떈 객체를 닫고 삭제
                            recording?.close()
                            recording = null
                            Log.e(TAG, "Video capture ends with error: " +
                                    "${recordEvent.error}")
                        }
                        videoCaptureButton.apply {
                            text = getString(R.string.start_capture)
                            isEnabled = true
                        }
                    }
                }
            }
        }
    }

    // CameraX Preview를 통해 카메라 화면 보여주기
    private fun startCamera() {
        // ProcessCameraProvider 인스턴스
        // 카메라의 수명 주기를 수명 주기 소유자와 바인딩하는 데 사용
        // CameraX가 수명 주기를 인식하므로 카메라를 열고 닫는 작업이 필요하지 않게됨
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener(
            {
                //  카메라의 수명 주기를 애플리케이션 프로세스 내의 LifecycleOwner에 바인딩하는 데 사용
                val cameraProvider = cameraProviderFuture.get()
                // 프리뷰 생성
                val preview = Preview.Builder()
                    .build()
                    .also {
                        it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                    }

                // 이미지 캡쳐를 위한 변수 초기화
                imageCapture = ImageCapture.Builder().build()

                // 비디오 캡쳐를 위한 변수 초기화
                val recorder = Recorder.Builder()
                    .setQualitySelector(QualitySelector.from(Quality.HIGHEST))
                    .build()

                videoCapture = VideoCapture.withOutput(recorder)

                // 전면 후면 선택 (후면을 디폴트로)
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                try {
                    // 카메라 연결하기 전 모든 카메라 객체를 해제
                    cameraProvider.unbindAll()
                    // 다시 카메라 연결
                    cameraProvider.bindToLifecycle(
                        this,
                        cameraSelector,
                        preview
                    )
                } catch (e: Exception) {
                    Log.e(TAG, "$e")
                }
            },
            // 실행기 등록
            ContextCompat.getMainExecutor(this)
        )
    }

    companion object {
        private const val TAG = "CameraXApp"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }

}
