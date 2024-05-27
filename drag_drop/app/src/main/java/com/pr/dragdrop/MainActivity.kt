package com.pr.dragdrop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
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
    @OptIn(ExperimentalGlideComposeApi::class)
    @Composable
    fun DragImage(url: String) {
        GlideImage(
            model = url,
            contentDescription = "Dragged Image"
        )
    }

    // 드롭 타겟이 될 이미지
    @OptIn(ExperimentalGlideComposeApi::class)
    @Composable
    fun DropTargetImage(url: String) {
        val urlState by remember { mutableStateOf(url) }
        GlideImage(
            model = urlState,
            contentDescription = "Dropped Image"
        )
    }
}
