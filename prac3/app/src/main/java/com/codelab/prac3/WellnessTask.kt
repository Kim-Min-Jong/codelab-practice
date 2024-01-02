package com.codelab.prac3

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class WellnessTask(
    val id: Int,
    val label: String,
    initialChecked: Boolean = false
) {
    // 선택 상태 로직도 뷰모델로 이관하기 위해 체크 상태 속성 추가
    var checked by mutableStateOf(false)
}
