package com.example.busschedule

import androidx.lifecycle.ViewModel
import com.example.busschedule.database.Schedule
import com.example.busschedule.database.ScheduleDao

// 데이터베이스에 접근할 뷰모델 정의

// ViewModel 클래스는 앱 UI 관련 데이터를 저장하는 데 사용되고 수명 주기도 인식하므로 활동이나 프래그먼트와 마찬가지로 수명 주기 이벤트에 응답
// 화면 회전과 같은 수명 주기 이벤트로 인해 활동이나 프래그먼트가 소멸되었다가 다시 생성되면 연결된 ViewModel을 다시 만들 필요가 없다.
// DAO 에 직접 액세스할 때는 불가능하므로 ViewModel 서브클래스를 사용하여 활동이나 프래그먼트에서 데이터를 로드하는 책임을 분리하는 것이 좋다.
class BusScheduleViewModel(
    private val scheduleDao: ScheduleDao
): ViewModel() {

    fun fullSchedule(): List<Schedule> = scheduleDao.getAll()

    fun scheduleForStopName(name: String): List<Schedule> = scheduleDao.getByStopName(name)

}
