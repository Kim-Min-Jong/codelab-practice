package com.pr.dragdropxml

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
/*
    드래그 앤 드롭 프로세스 (4단계)
    1. 시작됨: 시스템이 사용자의 드래그 동작에 응답하여 드래그 앤 드롭 작업을 시작
    2. 진행 중: 사용자가 계속 드래그하고, 타겟 뷰로 들어갈 때 dragshadow 빌더가 시작
    3. 끝남: 사용자가 경계 상자 안에서 드래그를 해제하고 드롭 타겟 영역에 타겟을 드롭
    4. 종료됨: 시스템이 드래그 앤 드롭 작업을 종료하라는 신호를 보냄


    시스템은 DragEvent 객체에서 드래그 이벤트를 전송, DragEvent 객체에는 다음 데이터가 포함 됨
    1. ActionType: 드래그 앤 드롭 이벤트의 수명 주기 이벤트에 기반한 이벤트의 작업 값. 예: ACTION_DRAG_STARTED, ACTION_DROP 등
    2. ClipData: 드래그되고 ClipData 객체에 캡슐화되는 데이터
    3. ClipDescription: ClipData 객체에 대한 메타 정보
    4. Result: 드래그 앤 드롭 작업의 결과
    5. X: 드래그된 객체의 현재 위치의 x 좌표
    6. Y: 드래그된 객체의 현재 위치의 y 좌표
 */

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
    }
}
