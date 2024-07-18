package com.pr.coroutinewithtest.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay

class RaceParticipant(
    val name: String,
    val maxProgress: Int = 100,
    val progressDelayMillis: Long = 500L,
    private val progressIncrement: Int = 1,
    private val initialProgress: Int = 0
) {

    init {
        require(maxProgress > 0) { "maxProgress=$maxProgress; must be > 0" }
        require(progressIncrement > 0) { "progressIncrement=$progressIncrement; must be > 0" }
    }

    /**
     * Indicates the race participant's current progress
     */
    var currentProgress by mutableStateOf(initialProgress)
        private set

    /**
     * Regardless of the value of [initialProgress] the reset function will reset the
     * [currentProgress] to 0
     */
    fun reset() {
        currentProgress = 0
    }

    // 진행률을 올리는 메소드
    suspend fun run() {
        // 100이 되기 전까지 반복 (딜레이를 주며)
        while (currentProgress <= maxProgress) {
            delay(progressDelayMillis)
            currentProgress += progressIncrement
        }
    }
}

// 현재 진행률
val RaceParticipant.progressFactor: Float
    get() = currentProgress / maxProgress.toFloat()
