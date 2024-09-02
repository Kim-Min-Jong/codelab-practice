package com.example.android.cars.roadreels

import androidx.compose.runtime.compositionLocalOf

// 앱 인셋 (뷰의 네 모서리의 크기를 가진 객체)을 조젓정
const val DEFAULT_CONTROLLABLE_INSETS = 0
val LocalControllableInsets = compositionLocalOf {
    DEFAULT_CONTROLLABLE_INSETS
}
