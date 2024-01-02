package com.codelab.prac3.viewmodel

import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import com.codelab.prac3.WellnessTask

/**
 * Compose에서 ViewModel 사용 시 주의할 점
 * -> ViewModel은 컴포지션의 일부가 아니다.
 * 따라서 메모리 누수가 발생할 수 있으므로 컴포저블에서 만든 상태(예: 기억된 값)를 보유해서는 안 된다.
 */
class WellnessViewModel: ViewModel() {
    private val _tasks = getWellnessTasks().toMutableStateList()
    val tasks: List<WellnessTask>
        get() = _tasks

    // 삭제 로직을 뷰모델로 이관
    fun remove(item: WellnessTask) {
        _tasks.remove(item)
    }

    // 체크 상태 처리 로직 뷰모델로 이관
    fun changeTaskChecked(
        item: WellnessTask,
        checked: Boolean
    ) {
        tasks.find { it.id == item.id }?.let { task ->
            task.checked = checked
        }
    }

    // 더미 테이터
    private fun getWellnessTasks() = List(30) { i -> WellnessTask(i, "Task # $i") }
}
