package com.codelab.prac4

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.codelab.prac4.ui.theme.Prac4Theme

/*
    Jetpack Compose의 고급 상태 및 부작용

    Jetpack Compose의 상태 및 부작용 API와 관련된 고급 개념을 알아봅니다.
    로직이 자명하지 않은 스테이트풀(Stateful) 컴포저블의 상태 홀더를 만드는 방법,
    Compose 코드에서 코루틴을 만들고 정지 함수를 호출하는 방법, 다양한 사용 사례를 달성하기 위해 부작용을 트리거하는 방법을 살펴보겠습니다.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Prac4Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Prac4Theme {

    }
}
