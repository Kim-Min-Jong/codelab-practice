/*
* Copyright (c) 2023 Google LLC
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      https://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.example.stylus

import android.os.Build
import android.view.MotionEvent
import androidx.compose.ui.graphics.Path
import androidx.lifecycle.ViewModel
import com.example.stylus.data.DrawPoint
import com.example.stylus.data.DrawPointType
import com.example.stylus.ui.StylusState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update


class StylusViewModel : ViewModel() {

    private var currentPath = mutableListOf<DrawPoint>()

    // 데이터
    private var _stylusState = MutableStateFlow(StylusState())
    val stylusState: StateFlow<StylusState>
        get() = _stylusState

    // 새로운 상태를 받아 렌더링 업데이트
    private fun requestRendering(state: StylusState) {
        _stylusState.update {
            return@update state
        }
    }

    // 그릴 경로 만들기
    private fun createPath(): Path {
        val path = Path()

        for (point in currentPath) {
            // 선의 시작점인지 이전위치가 있는지 확인 START - 시작점
            if (point.type == DrawPointType.START) {
                path.moveTo(point.x, point.y)
            } else {
                path.lineTo(point.x, point.y)
            }
        }

        return path
    }

    fun processMotionEvent(motionEvent: MotionEvent): Boolean {
        // Motion Event
        when (motionEvent.actionMasked) {
            // 포인터가 화면을 터치
            // MotionEvent 객체에서 보고한 위치에서 선이 시작
            MotionEvent.ACTION_DOWN -> {
                currentPath.add(
                    DrawPoint(motionEvent.x, motionEvent.y, DrawPointType.START)
                )
            }
            // 포인터가 화면에서 이동, 선이 그려짐
            MotionEvent.ACTION_MOVE -> {
                currentPath.add(DrawPoint(motionEvent.x, motionEvent.y, DrawPointType.LINE))
            }
            // 포인터가 화면 터치를 중지, 선이 끝남
            MotionEvent.ACTION_POINTER_UP, MotionEvent.ACTION_UP -> {
                // 취소된 동작인지 확인
                val canceled = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                        (motionEvent.flags and MotionEvent.FLAG_CANCELED) == MotionEvent.FLAG_CANCELED

                if (canceled) {
                    cancelLastStroke()
                } else {
                    currentPath.add(DrawPoint(motionEvent.x, motionEvent.y, DrawPointType.LINE))
                }
            }
            // 원치 않는 터치가 감지, 마지막 획을 취소
            MotionEvent.ACTION_CANCEL -> {
                // Unwanted touch detected.
                cancelLastStroke()
            }
            else -> return false
        }

        // 모션이벤트 완료후 렌더링 요청 시작
        requestRendering(
            StylusState(
                tilt = motionEvent.getAxisValue(MotionEvent.AXIS_TILT),
                pressure = motionEvent.pressure,
                orientation = motionEvent.orientation,
                path = createPath()
            )
        )

        return true
    }

    // 원치 않는 터치 감지 시 마지막을 취소함
    private fun cancelLastStroke() {
        val lastIndex = currentPath.findLastIndex {
            it.type == DrawPointType.START
        }

        if (lastIndex > 0) {
            // 마지막을 뺸 리스트를 다시 반환
            currentPath = currentPath.subList(0, lastIndex - 1)
        }
    }
}

inline fun <T> List<T>.findLastIndex(predicate: (T) -> Boolean): Int {
    val iterator = this.listIterator(size)
    var count = 1
    while (iterator.hasPrevious()) {
        val element = iterator.previous()
        if (predicate(element)) {
            return size - count
        }
        count++
    }
    return -1
}
