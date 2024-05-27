package com.pr.dragdrop

import android.content.ClipData
import android.content.ClipDescription
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.draganddrop.mimeTypes
import androidx.compose.ui.draganddrop.toAndroidDragEvent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.pr.dragdrop.ui.theme.DragdropTheme

/*
   Drag & Drop Event

    총 4단계의 이벤트로 볼 수 있다.
    1. 시작됨: 스템이 사용자의 드래그 동작에 응답하여 드래그 앤 드롭 작업을 시작
    2. 진행 중: 사용자가 계속 드래그하는 상태
    3. 종료됨: 사용자가 드롭 타겟 컴포저블에서 드래그를 해제(멈춤)
    4. 존재함: 시스템이 드래그 앤 드롭 작업을 종료하라는 신호를 보냄

    시스템은 DragEvent 객체에서 이벤트를 전송함
    다음과 같은 데이터가 포함되어 있음

    1. ActionType: 드래그 앤 드롭 이벤트의 수명 주기 이벤트에 기반한 이벤트의 작업 값
        예: ACTION_DRAG_STARTED, ACTION_DROP 등
    2. ClipData: 드래그되고 ClipData 객체에 캡슐화되는 데이터
    3. ClipDescription: ClipData 객체에 관한 메타 정보
    4. Result: 드래그 앤 드롭 작업의 결과
    5. X: 드래그된 객체의 현재 위치의 x 좌표
    6. Y: 드래그된 객체의 현재 위치의 y 좌표
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DragdropTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column {
                        DragImage(url = getString(R.string.source_url))
                        DropTargetImage(url = getString(R.string.target_url))
                    }
                }
            }
        }
    }

    // 드래그가 될 이미지
    @OptIn(ExperimentalGlideComposeApi::class, ExperimentalFoundationApi::class)
    @Composable
    fun DragImage(url: String) {
        GlideImage(
            model = url,
            contentDescription = "Dragged Image",
            // 적용되는 모든 요소의 드래그 앤 드롭 기능을 사용 설정
            modifier = Modifier.dragAndDropSource {
                // 드래그 동작인 길게 누르기를 감지
                detectTapGestures(
                    // 드래그되는 데이터의 전송을 시작
                    onLongPress = {
                        // 동작 완료 시 전송될 데이터로
                        // transferData를 사용하여 드래그 앤 드롭 세션을 시작
                        startTransfer(
                            // 1. ClipData: 전송될 실제 데이터
                            // 2. flags: 래그 앤 드롭 작업을 제어하는 플래그
                            // 3. localState: 동일한 활동에서 드래그할 때 세션의 로컬 상태
                            DragAndDropTransferData(
                                ClipData.newPlainText("image uri", url)
                            )
                        )
                    }
                )
            }
        )
    }

    // 드롭 타겟이 될 이미지
    @OptIn(ExperimentalGlideComposeApi::class, ExperimentalFoundationApi::class)
    @Composable
    fun DropTargetImage(url: String) {
        // 드롭 될 때 시각적인 효과를 보여주기 위한 색 변수
        var tintColor by remember {
            mutableStateOf(Color(0xffE5E4E2))
        }
        var urlState by remember { mutableStateOf(url) }
        val dndTarget = remember {
            object : DragAndDropTarget {
                // 다양한 메소드를 오버라이드 할 수 있는데 onDrop은 필수
                // 이미지가 드롭 됐을 때 실행 될 콜백
                override fun onDrop(event: DragAndDropEvent): Boolean {
                    val draggedData = event.toAndroidDragEvent()
                        .clipData
                        .getItemAt(0)
                        .text

                    // 변수 재할당
                    urlState = draggedData.toString()
                    return true
                }

                // 드롭 될 때 시각적인 효과를 위해 라이프사이클 별 색 변경
                override fun onEntered(event: DragAndDropEvent) {
                    super.onEntered(event)
                    tintColor = Color(0xff00ff00)
                }

                override fun onExited(event: DragAndDropEvent) {
                    super.onExited(event)
                    tintColor = Color(0xffE5E4E2)
                }

                override fun onEnded(event: DragAndDropEvent) {
                    super.onEnded(event)
                    tintColor = Color(0xffE5E4E2)
                }
            }
        }


        GlideImage(
            model = urlState,
            contentDescription = "Dropped Image",
            // 데이터를 드래그할 수 있도록 하는 Modifier
            modifier = Modifier.dragAndDropTarget(
                // 컴포저블이 세션을 시작한 DragAndDropEvent를 검사하여 지정된 드래그 앤 드롭 세션으로부터 수신할지 결정
                shouldStartDragAndDrop = { event ->
                    // 드래그 되는 항목 중 하나 이상이 일반 텍스트일 때 드롭 작업을 허용
                    event.mimeTypes()
                        .contains(ClipDescription.MIMETYPE_TEXT_PLAIN)
                },
                // 지정된 드래그 앤 드롭 세션의 이벤트를 수신하는 DragAndDropTarget
                target = dndTarget
            ),
            // 이미지에 색상을 추가
            colorFilter = ColorFilter.tint(
                color = tintColor,
                blendMode = BlendMode.Modulate
            )
        )
    }
}
