package com.codelab.prac2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.codelab.prac2.ui.theme.Prac2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Prac2Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SearchBar()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
// 검색 가능한 화면(컴포저블)을 생성 -- EditText in xml
fun SearchBar(
    modifier: Modifier = Modifier
) {
    TextField(
        // 기본 값
        value = "",
        // 값이 바뀔 때 호출되는 콜백
        onValueChange = {},
        // modifier를 통해 SearchBar의 모양을 바꿔볼 수 있음
        // 컴포저블의 크기, 레이아웃, 동작, 모양 변경
        // 접근성 라벨과 같은 정보 추가
        // 사용자 입력 처리
        // 요소를 클릭 가능, 스크롤 가능, 드래그 가능 또는 확대/축소 가능하게 만드는 것과 같은 높은 수준의 상호작용 추가

        // 최소 56dp의 높이, 가로는 상위요소의 전체 가로공간으로 설정
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp),

        // EditText의 왼쪽에 나타나는 아이콘을 출력
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                // TextField의 placeholder가 이미 텍스트 필드의 의미를 설명하므로 이 아이콘에는 콘텐츠 설명이 필요하지 않습니다.
                // contentDescription 은 주로 접근성을 위해 사용된다.
                contentDescription = null
            )
        },

        // TextFieldDefaults 의 textFieldColors를 통해 특정 색상을 재정의
        colors = TextFieldDefaults.textFieldColors(
            unfocusedSupportingTextColor = MaterialTheme.colorScheme.surface,
            focusedSupportingTextColor = MaterialTheme.colorScheme.surface
        ),


        // placeholder 지정 (미리보기)
        placeholder = {
            Text(stringResource(R.string.placeholder_search))
        },
    )
}

// 이미지를 포함한 Column 카드를 생성할 컴포저블
@Composable
fun AlignYourBodyElement(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        // 이미지 추가
        Image(
            // 이미지 리소스등록
            painter = painterResource(id = R.drawable.ab1_inversions),
            contentDescription = null
        )
        Text(text = stringResource(id = R.string.ab1_inversions))
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Prac2Theme {

    }
}
