package com.pr.dragdropxml

import android.content.ClipData
import android.content.ClipDescription
import android.os.Bundle
import android.util.Log
import android.view.DragEvent
import android.view.View
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.ViewCompat.startDragAndDrop
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.pr.dragdropxml.databinding.ActivityMainBinding

/*
    드래그 앤 드롭 프로세스 (4단계)
    1. 시작됨: 시스템이 사용자의 드래그 동작에 응답하여 드래그 앤 드롭 작업을 시작
    2. 진행 중: 사용자가 계속 드래그하고, 타겟 뷰로 들어갈 때 dragshadow 빌더가 시작
    3. 끝남: 사용자가 경계 상자 안에서 드래그를 해제하고 드롭 타겟 영역에 타겟을 드롭
    4. 종료됨: 시스템이 드래그 앤 드롭 작업을 종료하라는 신호를 보냄


    시스템은 DragEvent 객체에서 드래그 이벤트를 전송, DragEvent 객체에는 다음 데이터가 포함 됨
    1. ActionType: 드래그 앤 드롭 이벤트의 수명 주기 이벤트에 기반한 이벤트의 작업 값. 예: ACTION_DRAG_STARTED, ACTION_DROP 등
    2. ClipData: 드래그되고 ClipData 객체에 캡슐화되는 데이터
    3. ClipDescription: ClipData 객체에 대한 메타 정보
    4. Result: 드래그 앤 드롭 작업의 결과
    5. X: 드래그된 객체의 현재 위치의 x 좌표
    6. Y: 드래그된 객체의 현재 위치의 y 좌표
 */

class MainActivity : AppCompatActivity() {

    // view binding
    val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        with(binding) {
            // 초기 텍스트 및 이미지 세팅
            tvGreeting.text = getString(R.string.greeting)

            Glide.with(this@MainActivity).asBitmap()
                .load(getString(R.string.source_url))
                .into(ivSource)

            Glide.with(this@MainActivity).asBitmap()
                .load(getString(R.string.target_url))
                .into(ivTarget)

            startDrag(ivSource)
        }
    }

    private fun startDrag(draggableView: View) = with(binding) {
        draggableView.setOnLongClickListener { v ->
            // 드래그 로직을 작성

            // 드래그할 데이터 (ClipData 준비)
            // 데이터는 간단한 텍스트에서 동영상에 이르기까지 어떤 유형이든 될 수 있음
            // 이 데이터는 ClipData 객체에 캡슐화됨
            // ClipData 객체에 하나 이상의 복잡한 ClipItem이 있음 (캡슐화 됨)
            // ClipDescription을 통해 mimeType을 지정 가능
            val label = "Dragged Image Url"
            val clipItem = ClipData.Item(v.tag as? CharSequence)
            val mimeTypes = arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN)
            val draggedData = ClipData(label, mimeTypes, clipItem)

            // 드래그, 드롭 시작
            // startDragAndDrop(data, shadowBuilder, myLocalState, flag) 메소드 사용
            // 1. 데이터: ClipData. 형식으로 드래그되는 데이터
            // 2. shadowBuilder: DragShadowBuilder를 사용하여 뷰의 그림자 빌드
            // 3. myLocalState: 드래그 앤 드롭 작업에 관한 로컬 데이터가 포함된 객체
            // 4. flag: 드래그 앤 드롭 작업을 제어하는 플래그

            // 이 메소드가 호출 되면 View.DragShadowBuilder 기준으로 드래그 섀도우가 생김
            // 시스템에 드래그 섀도우가 있으면 OnDragListener 인터페이스를 뷰로 전송하여 드래그 드롭 작업이 시작
            v.startDragAndDrop(
                draggedData,
                View.DragShadowBuilder(v),
                null,
                0
            )

            true
        }
    }

    // 드롭될 이미지 처리
    private fun setUpDrop(dropTarget: View) {
        dropTarget.setOnDragListener { view, dragEvent ->
            // 드롭 될 로직

            // dragEvent - 단계별 작업이 존재
            // ACTION_DRAG_STARTED: 드래그 앤 드롭 작업의 시작을 알림
            // ACTION_DRAG_LOCATION: 사용자가 타겟 드롭 영역의 경계 안쪽이 아닌 들어온 상태에서 드래그를 해제했음을 나타냄
            // ACTION_DRAG_ENTERED: 드래그된 뷰가 타겟 드롭 뷰의 경계 안쪽에 있음을 나타냄
            // ACTION_DROP: 사용자가 타겟 드롭 영역에서 드래그를 해제했음을 나타냄
            // ACTION_DRAG_ENDED: 드래그 앤 드롭 작업이 완료되었음을 나타냄
            // ACTION_DRAG_EXITED: 드래그 앤 드롭 작업이 종료되었음을 나타냄

            when(dragEvent.action) {
                // 수신데이터가 올바른 지 확인
                DragEvent.ACTION_DRAG_STARTED -> {
                    if (dragEvent.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                        (view as? ImageView)?.alpha = 0.5f
                        view.invalidate()
                        true
                    } else {
                        false
                    }
                }

                // 드롭 된 데이터를 어떻게 할 지 처리
                DragEvent.ACTION_DROP -> {
                    // 드롭된 객체을 가져옴
                    val item = dragEvent.clipData.getItemAt(0)
                    val dragData = item.text
                    // 객체에서 이미지를 뽑아 출력
                    Glide.with(this).load(dragData)
                        .into(view as ImageView)
                    (view as? ImageView)?.alpha = 1.0f
                    true
                }

                // 드래그가 시작 될 때 시각적 효과 추가 - 이미지 투명도
                DragEvent.ACTION_DRAG_ENTERED -> {
                    (view as? ImageView)?.alpha = 0.3F
                    view.invalidate()
                    true
                }

                // 드롭 뷰의 밖으로 드래그 할 때의 시각 효과
                DragEvent.ACTION_DRAG_EXITED -> {
                    (view as? ImageView)?.alpha = 0.5F
                    view.invalidate()
                    true
                }

                // 끝났을 떄의 시각적 효과
                DragEvent.ACTION_DRAG_ENDED -> {
                    (view as? ImageView)?.alpha = 1.0F
                    true
                }

                else -> true
            }
        }
    }
}
