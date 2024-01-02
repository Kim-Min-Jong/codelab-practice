package com.codelab.prac3

import android.annotation.SuppressLint
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
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
@SuppressLint("UnrememberedMutableState")
@Composable
fun WaterCounter(
    modifier: Modifier = Modifier
) {

    Column(modifier = modifier.padding(16.dp)) {
        // 관찰 될 상태 카운터 값
        var count: MutableState<Int> = mutableStateOf(0)
        Text(
            text = "You've had ${count.value} glasses.",
        )
        Button(
            // 현재 상태에서는 버튼을 클릭해도 아무 일도 일어나지 않음
            // 상태가 변경될 때 Compose에 화면을 다시 그려야 한다고 알리지 않았기 떄문 (리컴포지션)

            // Compose 앱은 구성 가능한 함수를 호출하여 데이터를 UI로 변환
            // 컴포저블을 실행할 때 Compose에서 빌드한 UI에 관한 설명을 컴포지션이라 함
            // 상태가 변경되면 Compose는 영향을 받는 컴포저블 함수를 새 상태로 다시 실행. 그러면 리컴포지션된 UI가 만들어 짐

            // 컴포지션: 컴포저블을 실행할 때 Jetpack Compose에서 빌드한 UI에 관한 설명입니다.
            // 초기 컴포지션: 처음 컴포저블을 실행하여 컴포지션을 만듭니다.
            // 리컴포지션: 데이터가 변경될 때 컴포지션을 업데이트하기 위해 컴포저블을 다시 실행하는 것을 말합니다.

            // State 및 MutableStateOf를 사용하여 상태를 관찰

            onClick = { count.value++ },
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
