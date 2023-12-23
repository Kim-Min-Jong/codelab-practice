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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
                    val names: List<String> = listOf("World", "Compose")
                    // Compose 와 Kotlin의 통합
                    Column {
                        for (name in names) {
                            Greeting(name = name)
                        }
                    }
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
        // 상태 기억
        // remember는 리컴포지션을 방지하는데 사용됨
        // 상태가 재설정 되지 않음
        // 화면의 서로 다른 부분에서 동일한 컴포저블을 호출하는 경우, 호출하는 부분 자체적인 상태 버전을 가진 UI를 생성
        // 살짝 livedata 느낌???
        val expanded = remember { mutableStateOf(false) }

        // 버튼 클릭 시 UI 변화 주는 변수
        val extraPadding = if (expanded.value) 48.dp else 0.dp

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
                Column(modifier = Modifier.weight(1f).padding(bottom = extraPadding)) {
                    Text(text = "hello,")
                    Text(text = name)
                }

                // compose는 다양한 버튼 사양을 지원하는데
                // 여기서는 Text를 ElevatedButton으로 래핑하는 Elevated Button을 사용
                // https://m3.material.io/components/buttons/implementation/android
                ElevatedButton(
                    /**
                     *  Recomposition(리컴포지션)
                     *  Compose 앱은 구성 가능한 함수를 호출하여 데이터를 UI로 변환한다.
                     *  데이터가 변경되면 Compose는 새 데이터로 이러한 함수를 다시 실행하여 업데이트된 UI를 만든다.
                     *  이 과정을 리컴포지션이라고 한다.
                     *
                     *  Compose는 데이터가 변경된 요소만 다시 구성하고, 영향을 받지않은 요소는 건너뛴다.
                     *
                     *  @Composable에 내부 상태를 추가하려면 mutableStateOf를 통해 관리한다.
                     *  이 함수를 사용하면 Compose가 State를 읽는 함수를 재구성한다.
                     *
                     *  State 및 MutableState는 어떤 값을 보유하고 그 값이 변경될 때마다 UI 업데이트(리컴포지션)를 트리거하는 인터페이스입니다.
                     *
                     *  하지만 @Composable 내의 변수에 mutableStateOf만 할당해서는 할 수 없다.
                     *  변경가능한 새 상태로 상태를 재설정하여 컴포저블을 다시 호출할떄는 언제든 리컴포지션이 일어날 수 있기 떄문에
                     *  여러 리컴포지션간에 상태를 유지하려면 remember를 사용하여 변경 가능한 상태를 *기억*해야한다.
                     */
                    onClick = {
                        expanded.value = !expanded.value
                    }
                ) {
                    Text(text = if(expanded.value) "Show less" else "Show more")
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
