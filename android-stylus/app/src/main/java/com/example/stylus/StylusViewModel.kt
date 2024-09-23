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

import android.view.MotionEvent
import androidx.compose.ui.graphics.Path
import androidx.lifecycle.ViewModel
import com.example.stylus.data.DrawPoint
import com.example.stylus.data.DrawPointType


class StylusViewModel : ViewModel() {

    private var currentPath = mutableListOf<DrawPoint>()

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
            MotionEvent.ACTION_UP -> {
                currentPath.add(DrawPoint(motionEvent.x, motionEvent.y, DrawPointType.LINE))
            }
            // 원치 않는 터치가 감지, 마지막 획을 취소
            MotionEvent.ACTION_CANCEL -> {
                // Unwanted touch detected.
                cancelLastStroke()
            }
            else -> return false
        }

        return true
    }

    private fun cancelLastStroke() {
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
