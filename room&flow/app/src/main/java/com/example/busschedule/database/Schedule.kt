package com.example.busschedule.database

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// Room을 사용할 때, 각 테이블은 클래스로 만들 수 있다.
// Room과 같은 ORM 라이브러리에서는 이를 모델 클래스라부른다

// 엔티티(테이블) 지정
// 엔티티 이름도 설정 가능
@Entity
data class Schedule(
    // 기본 키 지정
    @PrimaryKey val id: Int,
    // null 값이 되지 않도록 nonnull 지정
    // 테이블 지정 되는 속성 이름을 ColumnInfo를 통해 지정
    @NonNull @ColumnInfo(name = "stop_name") val stopName: String,
    @NonNull @ColumnInfo(name = "arrival_time") val arrivalTime: Int,
    )
