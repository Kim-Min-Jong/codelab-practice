package com.codelab.codelab_prac_1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

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
//                    MyApp()
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
        Surface(
            color = MaterialTheme.colorScheme.primary,
            // 전체에 수평 수직 패딩 주기
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
        ) {
//            // 하나의 열 생성
//            // 너비를 채우고 패딩 주기
//            Column(modifier = Modifier
//                .fillMaxWidth() // 너비(가로) 전체 채우기, 기본 값은 전체 채우기이며 비율 설정 가능
//                .padding(24.dp)) {
//                // 열 안의 내부 요소 생성 - 열 모양으로 생성됨
//                Text(text = "hello,")
//                Text(text = name)
//            }

            // 버튼 연습
            Row(modifier = Modifier.padding(24.dp)) {
                /**
                 * 컴포저블을 행 끝에 배치하는 방법을 알아야함.
                 * alignEnd 수정자가 없으므로 시작 시 컴포저블에 약간의 weight을 제공합.
                 * weight 수정자는 요소를 유연하게 만들기 위해 가중치가 없는 다른 요소(유연성 부족이라고 함)를 효과적으로 밀어내어 요소의 사용 가능한 모든 공간을 채움.
                 */
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "hello,")
                    Text(text = name)
                }

                // compose는 다양한 버튼 사양을 지원하는데
                // 여기서는 Text를 ElevatedButton으로 래핑하는 Elevated Button을 사용
                // https://m3.material.io/components/buttons/implementation/android
                ElevatedButton(
                    onClick = {}
                ) {
                    Text(text = "Show more")
                }
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
                modifier = Modifier.fillMaxSize(), // 가로 세로 모두 전체 채우기, 비율로 설정도 가능
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
            // 화면 전체에 패딩 지정
            modifier = modifier.padding(vertical = 4.dp),
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
