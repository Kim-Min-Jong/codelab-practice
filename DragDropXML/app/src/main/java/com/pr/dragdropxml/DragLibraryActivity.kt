package com.pr.dragdropxml

import android.content.ClipData
import android.content.ClipDescription
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.component1
import androidx.core.util.component2
import androidx.core.view.ContentInfoCompat
import androidx.core.view.DragStartHelper
import androidx.draganddrop.DropHelper
import com.bumptech.glide.Glide
import com.pr.dragdropxml.databinding.ActivityDragLibraryBinding

class DragLibraryActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityDragLibraryBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        with (binding) {
            tvGreeting.text = getString(R.string.greeting)
            Glide.with(this@DragLibraryActivity).asBitmap()
                .load(getString(R.string.source_url_1))
                .into(binding.ivSource)
            Glide.with(this@DragLibraryActivity).asBitmap()
                .load(getString(R.string.target_url_1))
                .into(binding.ivTarget)
            ivSource.tag = getString(R.string.source_url_1)


            // DragHelper를 통해 간소화 가능
            DragStartHelper(ivSource) { v, _ ->
                val item = ClipData.Item(v.tag as? CharSequence)
                val dragData = ClipData(
                    v.tag as? CharSequence,
                    arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN),
                    item
                )

                v.startDragAndDrop(
                    dragData,
                    View.DragShadowBuilder(v),
                    null,
                    0
                )

                true
            }.attach()

            // DropHelper (받는 쪽)
            DropHelper.configureView(
                this@DragLibraryActivity,
                // 드롭될 곳
                ivTarget,
                // 텍스트 형식의데이터 받기
                arrayOf("text/*"),
                DropHelper.Options.Builder()
                    // 드롭 강조 과
                    .setHighlightColor(Color.BLUE)
                    .setHighlightCornerRadiusPx(16)
                    .build()
            ) {
                // 드롭된 데이터를 처리하는 인터페ㅣ스
                // payload - 드롭되는 실제 컨텐츠
                _, payload: ContentInfoCompat ->
                val item = payload.clip.getItemAt(0)
                val dragData = item.text
                Glide.with(this@DragLibraryActivity)
                    .load(dragData)
                    .centerCrop()
                    .into(ivTarget as ImageView)

                // Consume payload by only returning remaining items
                val (_, remaining) = payload.partition { it == item }
                remaining
            }
        }
    }
}
