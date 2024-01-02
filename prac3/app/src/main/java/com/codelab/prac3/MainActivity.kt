package com.codelab.prac3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.codelab.prac3.ui.theme.Prac3Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Prac3Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WellnessScreen()
                }
            }
        }
    }
}

// 워터 카운터를 호출하는 기본 컴포저블
@Composable
fun WellnessScreen(
    modifier: Modifier = Modifier
) {
    WaterCounter(modifier)
}

/**
 * 상태란??
 * 시간이 지남에 따라 변하는 값
 *
 * 상태가 업데이트되는 이유는 무엇일까요? Android 앱에서는 이벤트에 대한 응답으로 상태가 업데이트
 * -> 이벤트는 애플리케이션 외부 또는 내부에서 생성되는 입력 ex) 버튼 누르기 등으로 UI와 상호작용하는 사용자, 기타 요인(예: 새 값을 전송하는 센서 또는 네트워크 응답)
 *
 * 핵심 아이디어: 상태는 **존재**하고 이벤트는 **발생**한다.
 *
 * 이벤트 사이클
 * 1. 이벤트: 이벤트는 사용자 또는 프로그램의 다른 부분에 의해 생성됩니다.
 * 2. 상태 업데이트: 이벤트 핸들러가 UI에서 사용하는 상태를 변경합니다.
 * 3. 상태 표시: 새로운 상태를 표시하도록 UI가 업데이트됩니다.
 */


// 하루 동안 마신 물잔 개수를 계산하는 워터 카운터 컴포저블
@Composable
fun WaterCounter(
    modifier: Modifier = Modifier
) {

    Column(modifier = modifier.padding(16.dp)) {
        var count = 0
        Text(
            text = "You've had $count glasses.",
        )
        Button(
            onClick = { count++ },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text(text = "Add One")
        }
    }

}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Prac3Theme {
        WellnessScreen()
    }
}
