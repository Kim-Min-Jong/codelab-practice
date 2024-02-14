package com.example.unscramble

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GameViewModel: ViewModel() {

    // StateFlow
    // 현재 상태와 새로운 상태를 확인해 내보내는 옵저버블한 데이터 홀더 flow
    // value가 현재 상태값을 반영
    // 상태를 업데이트하고 stream으로 전송하려면 MutableStateFlow 의 value에 값을 할당
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState>
        get() = _uiState.asStateFlow()

    // Backing property
    // backing property를 사용하면 정확한 객체가 아닌 getter에서 어떤 것을 반환할 수 있음
    // 캡슐화의 간소버전 getter, setter

    // ViewModel 내부에서만 바뀔 변수
    private var _count = 0
    // UI에 보여지기만 할 변수 - UI 단에서는 수정 불가능 (val)
    val count: Int
        get() = _count
}
