package com.pr.mlkitvision

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.google.mlkit.vision.common.InputImage
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

    }

    private fun runTextRecognition() {

    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        TODO("Not yet implemented")
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}
