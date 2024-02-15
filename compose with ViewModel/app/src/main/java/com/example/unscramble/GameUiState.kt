package com.example.unscramble

// 단어 맞추기 게임 UI의 상태를 관리하기 위한 데이터 클래스
data class GameUiState(
    // 섞인 현재 단어
    val currentScrambledWord: String = "",
    // 추측이 틀렸는 지 확인
    val isGuessedWordWrong: Boolean = false,
    // 점수를 나타내는 변수
    val score: Int = 0,
    // 단어 수를 세는 변수
    val currentWordCount: Int = 1,
)
