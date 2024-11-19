package com.pr.mlkitvision

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import java.util.PriorityQueue

class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private val TAG: String = "MainActivity"
    private var mImageView: ImageView? = null
    private var mTextButton: Button? = null
    private var mFaceButton: Button? = null
    private val mSelectedImage: Bitmap? = null
    private var mGraphicOverlay: GraphicOverlay? = null

    // Max width (portrait mode)
    private val mImageMaxWidth: Int? = null

    // Max height (portrait mode)
    private val mImageMaxHeight: Int? = null

    /**
     * Number of results to show in the UI.
     */
    private val RESULTS_TO_SHOW: Int = 3

    /**
     * Dimensions of inputs.
     */
    private val DIM_BATCH_SIZE: Int = 1
    private val DIM_PIXEL_SIZE: Int = 3
    private val DIM_IMG_SIZE_X: Int = 224
    private val DIM_IMG_SIZE_Y: Int = 224

    private val sortedLabels = PriorityQueue<Map.Entry<String, Float>>(
        RESULTS_TO_SHOW
    ) { o1, o2 -> o1.value.compareTo(o2.value) }

    /* Preallocated buffers for storing image data. */
    private val intValues = IntArray(DIM_IMG_SIZE_X * DIM_IMG_SIZE_Y)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mImageView = findViewById(R.id.image_view)

        mTextButton = findViewById(R.id.button_text)
        mFaceButton = findViewById(R.id.button_face)

        mGraphicOverlay = findViewById(R.id.graphic_overlay)


        mTextButton!!.setOnClickListener { runTextRecognition() }
        mFaceButton!!.setOnClickListener { runFaceContourDetection() }
        val dropdown = findViewById<Spinner>(R.id.spinner)
        val items = arrayOf("Test Image 1 (Text)", "Test Image 2 (Face)")
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item, items
        )
        dropdown.adapter = adapter
        dropdown.onItemSelectedListener = this@MainActivity
    }

    private fun runFaceContourDetection() {
        // 분석할 이미지
        val image = InputImage.fromBitmap(mSelectedImage!!, 0)

        // 얼굴 윤곽 탐지 옵션
        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
            .build()

        mFaceButton?.isEnabled = false
        // 얼굴 탐지기
        val detector = FaceDetection.getClient(options)
        detector.process(image)
            .addOnSuccessListener {
                mFaceButton?.isEnabled = true
                processFaceContourDetectionResult(it)
            }
            .addOnFailureListener {
                mFaceButton?.isEnabled = true
                it.printStackTrace()

            }
    }

    // 얼굴 윤곽 감지 응답 처리
    private fun processFaceContourDetectionResult(faces: List<Face>) {
        if (faces.isEmpty()) {
            Toast.makeText(this@MainActivity, "No face found", Toast.LENGTH_SHORT).show()
            return
        }
        mGraphicOverlay?.clear()

        for (i in 0 until faces.size) {
            val face = faces[i]
            val faceGraphic = FaceContourGraphic(mGraphicOverlay)
            mGraphicOverlay?.add(faceGraphic)
            faceGraphic.updateFace(face)
        }
    }

    private fun runTextRecognition() {
        // 분석할 이미지
        val image = InputImage.fromBitmap(mSelectedImage!!, 0)
        // 텍스트 분석기
        val recognizer = TextRecognition.getClient()
        mTextButton?.isEnabled = false

        // 분석시작
        recognizer.process(image)
            // 성공 리스너
            .addOnSuccessListener {
                mTextButton?.isEnabled = true
                processTextRecognition(it)
            }
            // 실패 리스너
            .addOnFailureListener{
                mTextButton?.isEnabled = true
                it.printStackTrace()
            }
    }

    // 텍스트 인식 응답 처리
    private fun processTextRecognition(text: Text) {
        // 인식된 텍스트 뭉치
        val blocks = text.textBlocks

        if (blocks.size == 0) {
            Toast.makeText(this@MainActivity, "No text found", Toast.LENGTH_SHORT).show()
            return
        }

        // 화면 초기화
        mGraphicOverlay?.clear()

        // 최종 값을 그래픽으로 출력
        for (i in 0 until blocks.size) {
           val lines = blocks[i].lines
           for (j in 0 until lines.size) {
               val elements = lines[j].elements
               for (k in 0 until elements.size) {
                   val textGraphic = TextGraphic(mGraphicOverlay, elements[k])
                    mGraphicOverlay?.add(textGraphic)
               }
           }
        }


    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        TODO("Not yet implemented")
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}
