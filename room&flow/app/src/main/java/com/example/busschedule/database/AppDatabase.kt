package com.example.busschedule.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// DAO에 액세스하기 위한 프래그먼트의 모델과 DAO, 뷰 모델을 정의했지만 여전히 이러한 모든 클래스로 할 작업을 Room에 알려야 한다.
// 여기서 AppDatabase가 필요하다.
// Room을 사용하는 Android 앱은 RoomDatabase 클래스를 서브클래스로 분류하고 몇 가지 주요 책임이 있다.
// 1. 데이터베이스에서 정의되는 항목을 지정
// 2. 각 DAO 클래스의 단일 인스턴스 액세스 권한을 제공
// 3. 필요시 더미 데이터 생성

// RoomDatabase() 클래스를 상속받아 사용
// @Database 어노테이션을 주어 엔티티 및 버전 정보를 설정
@Database(entities = [Schedule::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    // DAO에 접근할 수 있도록 메서드 추가
    abstract fun scheduleDao(): ScheduleDao

    // Appdatabase는 경합 상태나 기타 잠재적 문제를 방지하려고 데이터베이스 인스턴스가 하나만 있는지 확인하려고 한다 (싱클턴 패턴 활용)
    // 싱글턴을 활용하기 위해 companion object를 통해 인스턴스를 생성
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // 데이터베이스 객체를 반환하는 메서드
        fun getDatabase(context: Context): AppDatabase =
            // 인스턴스를 반환하는데 null 이라면 새로운 객체를 생성
            // synchronized를 통해 단일 스레드만 객체에 접근해게 하는 방식 - 객체가 여러개 생기는 것을 방지
            INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    "app_database"
                ).createFromAsset("database_bus_schedule.db")
                    .build()

                // INSTANCE 변수에 설정 후
                INSTANCE = instance

                // 객체를 반환
                instance
            }
    }
}
