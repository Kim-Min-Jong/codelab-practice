package com.codelab.prac2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.codelab.prac2.ui.theme.Prac2Theme

class MainActivity : ComponentActivity() {
    data class DrawableStringPair(
        @DrawableRes val drawable: Int,
        @StringRes val text: Int
    )
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
                    AlignYourBodyElement(
                        drawable = R.drawable.ab1_inversions,
                        text = R.string.ab1_inversions
                    )
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
    @DrawableRes drawable: Int,
    @StringRes text: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        // 열의 중앙 정렬을 통해 이미지 및 텍스트를 중앙으로 맞춤
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 이미지 추가
        Image(
            // 이미지 리소스등록
            painter = painterResource(id = drawable),
            contentDescription = null,
            // 이미지 크기 및 형태 조정
            modifier = Modifier
                .
                    // 이미지 사이즈 변경
                size(88.dp)
                // clip 함수를 통해 이미지 형태 변경
                // 단순히 형태만 변경하면 이미지가 잘릴 수 있음
                .clip(CircleShape),
            // contentScale 을 통해 scaleType을 지정하여 이미지 잘림 방지
            contentScale = ContentScale.Crop
        )
        Text(
            text = stringResource(id = text),
            // 텍스트의 기준선 (글자 바로 밑)으로 부터 여백(패딩 지정)
            modifier = Modifier.paddingFromBaseline(top = 24.dp, bottom = 8.dp),
            // style 지정 - 글씨 굵기
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun FavoriteCollectionCard(
    @DrawableRes drawable: Int,
    @StringRes text: Int,
    modifier: Modifier = Modifier
) {
    // 전체적으로 다른 배경색상을 주기 위해 Row를 Surface로 감쌈
    Surface(
        // Surface의 테두리를 둥글게 처리
        shape = MaterialTheme.shapes.medium,
        modifier = modifier
    ) {
        // 행 형태의 UI 생성
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.width(255.dp)
        ) {
            Image(
                painter = painterResource(drawable),
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                contentScale = ContentScale.Crop
            )

            Text(
                text = stringResource(text),
                style = MaterialTheme.typography.titleMedium,
                // 텍스트에 패딩 부여
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

// 리사이클러뷰를 만들 컴포저블
@Composable
fun AlignYourBodyRow(
    modifier: Modifier = Modifier
) {
    // orientation: horizontal 인 리사이클러뷰 - LazyRow
    // 단순히 수평으로 출력만 될 뿐, 상세 배치 형태는 없음
    // 카드 사이의 간격 등으르 조정하려면 배치에 대해 알아야함
    // Row, Column에 따라 가로축, 세로축에 대해 배치를 해볼수있음
    // Equal Weight, Space Between, Space Around... 등이 있음
    LazyRow(
        // 각 항목 사이에 8dp의 간격을 추가하기 위해 spacedBy 메서드를 사용
        horizontalArrangement =  Arrangement.spacedBy(8.dp),
        // 각 항목 사이에 간격을 주었을 때, 맨 앞과 뒤에는 간격이 지정되지 않았음으로 부여
        contentPadding = PaddingValues(horizontal = 16.dp),
        modifier = modifier
    ) {
        val alignYourBodyData = listOf(
            R.drawable.ab1_inversions to R.string.ab1_inversions,
            R.drawable.ab2_quick_yoga to R.string.ab2_quick_yoga,
            R.drawable.ab3_stretching to R.string.ab3_stretching,
            R.drawable.ab4_tabata to R.string.ab4_tabata,
            R.drawable.ab5_hiit to R.string.ab5_hiit,
            R.drawable.ab6_pre_natal_yoga to R.string.ab6_pre_natal_yoga
        ).map { MainActivity.DrawableStringPair(it.first, it.second) }

        items(alignYourBodyData) { item ->
            AlignYourBodyElement(item.drawable, item.text)
        }
    }
}

// 그리드 형태의 리사이클러뷰를 만들 컴포저블
@Composable
fun FavoriteCollectionsGrid(
    modifier: Modifier = Modifier
) {
    val favoriteCollectionsData = listOf(
        R.drawable.fc1_short_mantras to R.string.fc1_short_mantras,
        R.drawable.fc2_nature_meditations to R.string.fc2_nature_meditations,
        R.drawable.fc3_stress_and_anxiety to R.string.fc3_stress_and_anxiety,
        R.drawable.fc4_self_massage to R.string.fc4_self_massage,
        R.drawable.fc5_overwhelmed to R.string.fc5_overwhelmed,
        R.drawable.fc6_nightly_wind_down to R.string.fc6_nightly_wind_down
    ).map { MainActivity.DrawableStringPair(it.first, it.second) }
    // 수평 그리드 형태의 리사이클러뷰
    LazyHorizontalGrid(
        // row의 갯수를 얼마만큼 할 것인지 지정
        rows = GridCells.Fixed(2),
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(favoriteCollectionsData) { item ->
            FavoriteCollectionCard(item.drawable, item.text, modifier = Modifier.height(80.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Prac2Theme {

    }
}
