package com.example.busschedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.busschedule.database.ScheduleDao

// 뷰모델은 직접 인스턴스화를 할 수 없어서 다른 방법을 사용해야한다.
// ViewModel 클래스 BusScheduleViewModel은 수명 주기를 인식해야 하므로 수명 주기 이벤트에 응답할 수 있는 객체로 인스턴스화해야한다.
// 프래그먼트 중 하나에서 직접 인스턴스화하면 프래그먼트 객체가 앱 코드의 기능 범위를 벗어나는 모든 메모리 관리 등 모든 것을 처리해야 한다.
// 대신 뷰 모델 객체를 인스턴스화하는 팩토리라는 클래스를 만들 수 있다.

// activity-ktx, fragment-ktx 등 라이브러리로 위임자를 통해 팩토리 선언을 하지 않고도 사용할 수 있다.
class BusScheduleViewModelFactory(
    private val scheduleDao: ScheduleDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BusScheduleViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BusScheduleViewModel(scheduleDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
