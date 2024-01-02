package com.codelab.prac3

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.codelab.prac3.ui.theme.Prac3Theme

// https://developer.android.com/codelabs/jetpack-compose-state?hl=ko#0
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
        // 하지만 여전히 동작하지 않음 --> Column안에 있기 때문에 리컴포지션이 발생하면 count가 다시 초기화 되기 때문에
        // 이를 해결하기 위해 remember를 사용할 수 있음
        // remember로 계산된 값은 초기 컴포지션 중에 컴포지션에 저장되고 리컴포지션간에 우지됨

        // 참고:  이미 LiveData, StateFlow, Flow, RxJava의 Observable과 같은 다른 관찰 가능한 유형을 사용하여 상태를 앱에 저장하고 있을 수 있다.
        // Compose에서 이 상태를 사용하고 상태가 변경될 때 자동으로 재구성하도록 하려면 이를 State<T>에 매핑해야 한다
        // rememberSaveable을 톻해 화면전환 등 데이터가 사라질 상황이 되도 데이터를 유지하도록함
        var count by rememberSaveable { mutableStateOf(0) }

        if (count > 0) {
            // 추가 될 떄마다 Wellness 아이템을 추가
            var showTask by remember {
                mutableStateOf(true)
            }
            if (showTask) {
                WellnessTaskItem(
                    taskName = "Have you taken your 15 minute walk today?",
                    // 닫기 번튼을 누르면 false 로 변경되어 작업이 더 이상 표시되지 않게 함
                    onClose = { showTask = false }
                )
            }
            Text(
                text = "You've had $count glasses.",
            )
        }
        // Compose는 선언형 UI 프레임워크이다.
        // 상태가 변경될 때 UI 구성요소를 삭제하거나 공개 상태를 변경하는 대신 특정 상태의 조건에서 UI가 어떻게 존재하는지 설명한다.
        // 재구성이 호출되고 UI가 업데이트된 결과, 컴포저블이 결국 컴포지션을 시작하거나 종료할 수 있다.
        // 이 접근 방식을 사용하면 뷰 시스템과 마찬가지로 뷰를 수동으로 업데이트하는 복잡성을 방지할 수 있다.
        // 새 상태에 따라 뷰를 업데이트하는 일이 자동으로 발생하므로(개발자가 기억할 필요가 없음) 오류도 적게 발생한다.
        Row(Modifier.padding(top = 8.dp)) {
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

                onClick = { count++ },
                modifier = Modifier.padding(top = 8.dp),
                // 값의 조건에 따라 버튼 조작
                enabled = count < 10
            ) {
                Text(text = "Add One")
            }

            // 모든 아이템을 클리어하는 버튼
            Button(
                onClick = { count = 0 },
                Modifier.padding(start = 8.dp, top = 8.dp)
            ) {
                Text("Clear water count")
            }
        }

    }
}

// 사용자가 물 한 잔 이상 마셧을때, 표시를 하고, 닫을 컴포저블
// remeber 활용 - 닫기 버튼...
@Composable
fun WellnessTaskItem(
    taskName: String,
    // 닫기 아이콘 수신 콜백 람다
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp),
            text = taskName
        )
        IconButton(onClick = onClose) {
            Icon(Icons.Filled.Close, contentDescription = "Close")
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
