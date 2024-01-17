package com.example.busschedule.database

import androidx.room.Dao
import androidx.room.Query

// DAO (Data Access Object)
// DAO는 데이터 액세스 객체를 나타내며 데이터 액세스 권한을 제공하는 Kotlin 컴포넌트이다.
// 특히 DAO에는 데이터를 읽고 조작하는 함수가 포함된다. DAO에서 함수를 호출하는 것은 데이터베이스에서 SQL 명령어를 실행하는 것과 같다.

// Dao 어노테이션을 붙이면 Room이 인식한다.
@Dao
interface ScheduleDao {
    // @Quert 어노테이션을 사용하면 직접 쿼리 작성이 가능하다.
    @Query("SELECT * FROM schedule ORDER BY arrival_time ASC")
    fun getAll(): List<Schedule>

    // : 콜론을 붙여 쿼리에서 매개변수 참조가 가능하다.
    @Query("SELECT * FROM schedule WHERE stop_name = :stopName ORDER BY arrival_time ASC")
    fun getByStopName(stopName: String): List<Schedule>
}
