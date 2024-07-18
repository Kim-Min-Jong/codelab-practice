package com.pr.coroutinewithtest

import com.pr.coroutinewithtest.ui.RaceParticipant
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Test

import org.junit.Assert.*

class RaceParticipantTest {
    private val raceParticipant = RaceParticipant(
        name = "hong"
    )

    // 레이스 시작시 변경사함 테스트
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun raceParticipant_RaceStarted_ProgressUpdated() = runTest {
        // 읽기 전용 프로그레스 변수
        val expectedProgress = 1
        // 레이스 시뮬레이션 호출
        launch {
            raceParticipant.run()
        }
        // progressDelayMillis 만큼 시간을 진행해 테스트 실행시간 단축
        advanceTimeBy(raceParticipant.progressDelayMillis)
        // advanceTimeBy는 예약 작업을 실행하지 않으므로 직접 함수호출을 통해 대기중인 작업을 실행해야함
        runCurrent()
        // 시작값은 1, 진행률 확인을 위해 assert함수를 통해 일치하는 지 확인
        assertEquals(expectedProgress, raceParticipant.currentProgress)
    }

    // 레이스 종료시 변경사함 테스트
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun raceParticipant_RaceFinished_ProgressUpdated() = runTest {
        launch { raceParticipant.run() }
        advanceTimeBy(raceParticipant.maxProgress * raceParticipant.progressDelayMillis)
        runCurrent()
        // 최대값은 100이므로 100과 현재 진행률 확인
        assertEquals(100, raceParticipant.currentProgress)

    }
}
