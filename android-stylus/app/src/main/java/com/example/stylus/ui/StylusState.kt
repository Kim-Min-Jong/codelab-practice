package com.example.stylus.ui

import androidx.compose.ui.graphics.Path

data class StylusState (
    // 굵기
    val pressure: Float = 0f,
    // 방향 (외부로의)
    val orientation: Float = 0f,
    // 기울기
    val tilt: Float = 0f,
    // 렌더링된 선을 drawPath 메서드를 사용하여 저장
    val path: Path = Path(),
)
