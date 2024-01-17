package com.example.busschedule

import androidx.lifecycle.ViewModel
import com.example.busschedule.database.Schedule
import com.example.busschedule.database.ScheduleDao
import kotlinx.coroutines.flow.Flow

// 데이터베이스에 접근할 뷰모델 정의

// ViewModel 클래스는 앱 UI 관련 데이터를 저장하는 데 사용되고 수명 주기도 인식하므로 활동이나 프래그먼트와 마찬가지로 수명 주기 이벤트에 응답
// 화면 회전과 같은 수명 주기 이벤트로 인해 활동이나 프래그먼트가 소멸되었다가 다시 생성되면 연결된 ViewModel을 다시 만들 필요가 없다.
// DAO 에 직접 액세스할 때는 불가능하므로 ViewModel 서브클래스를 사용하여 활동이나 프래그먼트에서 데이터를 로드하는 책임을 분리하는 것이 좋다.
class BusScheduleViewModel(
    private val scheduleDao: ScheduleDao
): ViewModel() {

    // fragment에서 submitList가 호출 될 떄마다 데이터를 가져와서 보여즈도록 되어있지만
    // 앱에서는 아직 동적으로 업데이트를 할 수 없다.
    // 앱에서는 데이터가 변경되지 않았다고 보기 때문이다.
    // 변경사항을 확인하려면 앱을 다시 시작해야한다.

    // 문제는 dao에서 List<Schedule>이 한번만 반환된다는 것이다.
    // 기본 데이터가 업데이트되더라도 UI를 업데이트하려고 submitList()가 호출되지 않으므로 사용자 관점에서는 아무것도 변경되지 않은 것처럼 보인다.

    // 문제를 해결하려면 DAO가 데이터베이스에서 데이터를 지속적으로 내보낼 수 있는 비동기 flow라는 Kotlin 기능을 활용해야한다.
    // 데이터가 들어오거나 업데이트, 삭제 되면  그 결과가 프래그먼트로 다시 전송된다.
    // 이때 collect()라는 함수를 사용하면 새값을 받아 submitList를 호출해 변경사항을 나타낼 수 있다.
    fun fullSchedule(): Flow<List<Schedule>> = scheduleDao.getAll()

    fun scheduleForStopName(name: String): Flow<List<Schedule>> = scheduleDao.getByStopName(name)

}
