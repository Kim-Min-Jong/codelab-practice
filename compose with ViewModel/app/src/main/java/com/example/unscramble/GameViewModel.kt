package com.example.unscramble

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.unscramble.data.SCORE_INCREASE
import com.example.unscramble.data.allWords
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class GameViewModel: ViewModel() {

    init {
        // 뷰모델 초기화시 한번 단어와 상태를 초기화
        resetGame()
    }

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

    // 현재 단어 변수
    private lateinit var currentWord: String

    // 사용된 단어를 저장하는 집합
    private var usedWords: MutableSet<String> = mutableSetOf()

    // 사용자가 추축하는 단어
    var userGuess by mutableStateOf("")
        private set

    // 임의 단어를 선택하는 함수
    private fun pickRandomWordAndShuffle(): String {
        currentWord = allWords.random()
        return if (usedWords.contains(currentWord)) {
            pickRandomWordAndShuffle()
        } else {
            usedWords.add(currentWord)
            shuffleCurrentWord(currentWord)
        }
    }

    // 임의 단어의 철자들을 섞는 함수
    private fun shuffleCurrentWord(word: String): String {
        val tmpWord = word.toCharArray()
        tmpWord.shuffle()
        // 섞었을 때도 같으면 다를 떄 까지 계속 섞기
        while(String(tmpWord) == word) {
            tmpWord.shuffle()
        }
        return String(tmpWord)
    }

    // 초기화
    fun resetGame() {
        usedWords.clear()
        _uiState.value = GameUiState(currentScrambledWord = pickRandomWordAndShuffle())
    }

    fun updateUserGuess(guessedWord: String) {
        userGuess = guessedWord
    }

    // user의 단어를 확인하는 메소드
    fun checkUserGuess() {
        if (userGuess.equals(currentWord, ignoreCase = true)) {
            // 추측이 맞다면 점수를 증가
            val updatedScore = _uiState.value.score.plus(SCORE_INCREASE)
            // 상태를 변경
            updateGameState(updatedScore)
        } else {
            // 틀릴 경우 에러 보여줌
            _uiState.update { currentState ->
                currentState.copy(isGuessedWordWrong = true)
            }
        }
        // Reset user guess
        updateUserGuess("")
    }

    // 게임 상태가 바꿈을 나타내는 함수
    private fun updateGameState(updatedScore: Int) {
        // State를 update
        _uiState.update { currentState ->
            // 바뀐 것만 카피해서 업데이트
            // 점수를 업데이트하고 현재 단어 수를 늘리고 새 단어를 선택
            currentState.copy(
                isGuessedWordWrong = false,
                currentScrambledWord = pickRandomWordAndShuffle(),
                score = updatedScore,
                currentWordCount = currentState.currentWordCount.inc(),
            )

        }
    }
}
