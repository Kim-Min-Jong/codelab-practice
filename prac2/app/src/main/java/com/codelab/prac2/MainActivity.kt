package com.codelab.prac2

import android.content.res.Configuration
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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
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
import java.net.PortUnreachableException

class MainActivity : ComponentActivity() {
    data class DrawableStringPair(
        @DrawableRes val drawable: Int,
        @StringRes val text: Int
    )

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val windowSize = calculateWindowSizeClass(this)
            MySootheApp(windowSize = windowSize)
        }
//        newContent { MySootheAppPortrait() }
    }

    private fun newContent(
        type: @Composable () -> Unit
    ) {
        setContent {
            Prac2Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    type()
                }
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {//세로 전환시        Log.d("onConfigurationChanged" , "Configuration.ORIENTATION_PORTRAIT");    }else if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){ //가로전환시        Log.d("onConfigurationChanged", "Configuration.ORIENTATION_LANDSCAPE");    }else{            }
            newContent { MySootheAppPortrait() }
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            newContent { MySootheAppLandScape() }
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
        horizontalArrangement = Arrangement.spacedBy(8.dp),
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
        modifier = modifier.height(176.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(favoriteCollectionsData) { item ->
            FavoriteCollectionCard(item.drawable, item.text, modifier = Modifier.height(80.dp))
        }
    }
}

// 컴포넌트를 배치하가 위해 컨테이너형식을 만들어 줄 필요가 있는데
// 컴포즈에서는 슬롯 기반 레이아웃(슬롯 API)를 통해 구현해 볼 수 있다.
// 슬롯 기반 레이아우슨 개발자가 원하느대로 채울 수 있도록 UI에 빈 공간을 남겨둔다. -> 유연한 레이아웃 만들 수 있음
@Composable
fun HomeSection(
    @StringRes title: Int,
    modifier: Modifier = Modifier,
    // 컴포저블의 슬롯으로 content 매개변수를 사용 - 람다 활용
    content: @Composable () -> Unit
) {
    Column(modifier) {
        Text(
            text = stringResource(title),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .paddingFromBaseline(top = 40.dp, bottom = 16.dp)
                .padding(horizontal = 16.dp)
        )
        // 이렇게 하면 content에 컴포저블을 넣어줌으로써 컨테이너처럼 컴포저블을 쌓을 수 있음
        content()
    }
}

// 전체적인 화면 구성
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier
) {
    // Column 내부에 컴포저블을 배치
    // Lazy- 는 자동으로 스크롤을 추가하지만 일반적인 column, row는 스크롤이 없음
    // verticalScroll or horizontalScroll을 통해 수동으로 추가해주어야함
    // 이를 위해서는 스크롤의 현재 상태를 포함하며 외부에서 스크롤 상태를 수정하는 데 사용되는 ScrollState가 필요
    // 여기서는 스크롤 상태를 수정할 필요가 없으므로 rememberScrollState를 사용하여 영구 ScrollState 인스턴스를 활용
    Column(modifier.verticalScroll(rememberScrollState())) {
        // Spacer를 통해 공백 생성
        Spacer(Modifier.height(16.dp))
        // 검색 창
        SearchBar(Modifier.padding(horizontal = 16.dp))
        // 첫번째 섹션
        HomeSection(title = R.string.align_your_body) {
            AlignYourBodyRow()
        }
        // 두번째 섹션
        HomeSection(title = R.string.favorite_collection) {
            FavoriteCollectionsGrid()
        }
        Spacer(Modifier.height(16.dp))
    }
}

// 메뉴 탐색을 위한 bottom navigation view 생성
@Composable
fun SootheBottomNavigation(
    modifier: Modifier = Modifier
) {
    // Navigation 틀 생성
    NavigationBar(
        // 바텀 네비게이션의 컨테이너 (배경색) 변경
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier
    ) {
        // Navigation 아이템 (각 메뉴 생성
        NavigationBarItem(
            // 선택되었는지
            selected = true,
            // 클릭 했을 때의 동작 람다
            // 아이콘 설정
            icon = {
                Icon(
                    imageVector = Icons.Default.Spa,
                    contentDescription = null
                )
            },
            // 아이콘 아래 들어갈 메뉴 텍스트
            label = {
                Text(
                    text = stringResource(R.string.bottom_navigation_home)
                )
            },
            onClick = { }
        )        // Navigation 아이템 (각 메뉴 생성
        NavigationBarItem(
            // 선택되었는지
            selected = false,
            // 클릭 했을 때의 동작 람다
            // 아이콘 설정
            icon = {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null
                )
            },
            // 아이콘 아래 들어갈 메뉴 텍스트
            label = {
                Text(
                    text = stringResource(R.string.bottom_navigation_profile)
                )
            },
            onClick = { }
        )
    }
}

// BottomNavigation을 포함하는 전체 화면 구성
// Scaffold를 활용
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MySootheAppPortrait() {
    Prac2Theme {
        // Scaffold는 Material Design을 구현하는 앱을 위한 구성 가능한 최상위 수준 컴포저블을 제공
        // Material 개념의 슬롯이 포함되어 있는데, 그중 하나가 BottomNavigation
        Scaffold(
            // BottomNavigation 지정 속성
            bottomBar = { SootheBottomNavigation() }
        ) { padding ->
            HomeScreen(Modifier.padding(padding))
        }
    }
}

// 가로모드 UI
@Composable
fun MySootheAppLandScape() {
    Prac2Theme {
        // Row를 통해 행 형식으로 배치
        Surface(color = MaterialTheme.colorScheme.background) {
            Row {
                SootheNavigationRail()
                HomeScreen()
            }
        }
    }
}

// 가로모드를 만들었지만, 실제에서는 표시되지 않음
// 언제 어떤 모드르 보여줄 지 알려줘야하기 때문에....
// 이럴때 calculateWindowSizeClass()를 활용
// 창 너비에는 소형, 중형, 확장형이 있음
// 세로 모드인 경우 소형, 가로 모드인 경우 확장형임 이것을 통해 구분 (여기서 중형은 다루지않음)
@Composable
fun MySootheApp(
    windowSize: WindowSizeClass
) {
    when(windowSize.widthSizeClass) {
        WindowWidthSizeClass.Compact -> {
            MySootheAppPortrait()
        }
        WindowWidthSizeClass.Expanded -> {
            MySootheAppLandScape()
        }
    }
}

// 앱 레이아웃을 만들 때는 휴대전화의 가로 모드를 비롯한 여러 구성에서 앱이 어떻게 표시될지도 고려해야 한다.
// 다음은 가로 모드의 앱 디자인이다.
// Bottom Navigation 화면 콘텐츠 왼쪽의 레일로 전환되는 방식이다.
// 이를 구현하려면 Compose Material 라이브러리의 일부이며 BottomNavigation 메뉴를 만드는 데 사용된 NavigationBar와 유사하게 구현되는 NavigationRail을 사용
// NavigationRail 컴포저블 내에서 홈 및 프로필의 NavigationRailItem 요소를 추가한다.
@Composable
fun SootheNavigationRail(
    modifier: Modifier = Modifier
) {
    // 상위 레일을 두고
    NavigationRail(
        // 양 옆 패딩을 주고
        modifier = modifier.padding(start = 8.dp, end = 8.dp),
        // 배경색을 변경
        containerColor = MaterialTheme.colorScheme.background,
    ) {
        // 아래 열을 두고
        Column(
            // 높이를 열 전체 높이에 맞추고
            modifier = modifier.fillMaxHeight(),
            // 맨 위에 놓여 있는데 중앙에 배치
            /**
             * Arrangement -> 배치, gravity or chain style 처럼 생각
             */
            verticalArrangement = Arrangement.Center,
            // 수평 중앙으로 정렬
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 그 아래 각 레일 아이템을 둠
            NavigationRailItem(
                icon = {
                    Icon(
                        imageVector = Icons.Default.Spa,
                        contentDescription = null
                    )
                },
                label = {
                    Text(stringResource(R.string.bottom_navigation_home))
                },
                selected = true,
                onClick = {}
            )

            NavigationRailItem(
                icon = {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = null
                    )
                },
                label = {
                    Text(stringResource(R.string.bottom_navigation_profile))
                },
                selected = false,
                onClick = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Prac2Theme {

    }
}
