package com.codelab.codelab_prac_1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContent 내부에 정의를 하여 뷰를 구현함 -> xml 대신
        setContent {
            // 내부에서 기본 앱 테마를 설정함
            // 여기서는 따로 구현한 테마를 사용
            BasicsCodelabTheme {
                // A surface container using the 'background' color from the theme
                // Surface는 Material design 시스템에 기반한 Compose의 컨테이너, UI와 환경을 만드는데 기여함
                // 일반적으로 카드 또는 패널과 같은 요소를 나타내는데 사용된다. 그림자, 경계선, 배경색 등과 같은 시각적 효과를 자동으로 처리하며, 표면에 콘텐츠를 배치하는데 사용됨
                Surface(
                    // 해당 compose 컴포넌트의 수정자
                    // 상위 요소 레이아웃 내에서 UI요소가 배치되고 동작하는 방식을 해당 요소에 전달
                    // 여기서는 화면에 꽉차게하도록 만듦
                    // 요소 목록 https://developer.android.com/jetpack/compose/modifiers-list?hl=ko
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }

    // compose ui를 사용하려면 Composable 어노테이션을 선언해야함
    // @Composable은 클래스단위에는 붙일 수 없고, 함수 단위에 붙일 수 있음
    // @Composable을 붙어야 Text 같이 선언형 UI를 구현할 수 있음
    @Composable
    fun Greeting(name: String) {
//        Text(text = "Hello $name!")
        // row, column - 행 열 만들기
        Surface(color = MaterialTheme.colorScheme.primary) {
            // 하나의 열 생성
            Column {
                // 열 안의 내부 요소 생성 - 열 모양으로 생성됨
                Text(text = "hello,")
                Text(text = name)
            }
        }
    }

    // @Preview는 Android Studio 미리보기를 사용할 수 있음
    // 이 역시 @Composable이 필요함
    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        BasicsCodelabTheme {
            // A surface container using the 'background' color from the theme
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                Greeting("Android")
                MyApp()
            }
        }
    }

    /**
     * 함수는 기본적으로 빈 수정자가 할당되는 수정자 매개변수를 포함하는 것이 좋습니다.
     * 함수 내에서 호출하는 첫 번째 컴포저블로 이 수정자를 전달합니다.
     * 이렇게 하면 호출 사이트가 구성 가능한 함수 외부에서 레이아웃 안내와 동작을 조정할 수 있습니다.
     */
    @Composable
    private fun MyApp(
        modifier: Modifier = Modifier,
        names: List<String> = listOf("World", "Compose")
        ) {
        Surface(
            modifier = modifier,
            color = MaterialTheme.colorScheme.background
        ) {
            Greeting("Android")

            // Compose 와 Kotlin의 통합
            Column(modifier) {
                for (name in names) {
                    Greeting(name = name)
                }
            }
        }
    }
}
