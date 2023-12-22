package com.codelab.codelab_prac_1

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Set of Material typography styles to start with
// 텍스트 스타일 지정
val Typography = Typography(
    bodyLarge = TextStyle(
        // 폰트 스타일
        fontFamily = FontFamily.Default,
        // 폰트 굵기
        fontWeight = FontWeight.Normal,
        // 폰트 사이즈
        fontSize = 16.sp,
        // 텍스트 라인 높이
        lineHeight = 24.sp,
        // 자간
        letterSpacing = 0.5.sp
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)
