/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.advancedcoroutines

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * The Data Access Object for the Plant class.
 */
@Dao
interface PlantDao {
    // flow로 리턴 타입을 바꾸면 room은 다음과 같은 특성으로 쿼리를 실행
    // 1. 기본 안전성 - Flow 반환 유형을 사용하는 쿼리는 항상 Room 실행기로 실행되므로 항상 기본 안전성이 보장
    //  기본 스레드에서 실행하기 위해 코드에 아무 작업도 하지 않아도됨
    // 2. 변경사항 관찰 – Room은 변경사항을 자동으로 관찰하고 flow에 새 값을 보냄
    // 3. 비동기 시퀀스 - Flow는 각 변경 시 전체 쿼리 결과를 내보내며 어떠한 버퍼도 발생하지 않음
    //  Flow<List<T>>를 반환하면 흐름은 쿼리 결과의 모든 행을 포함하는 List<T>를 내보냄
    //  즉, 쿼리 결과를 한 번에 하나씩 내보내고 다음 쿼리에 대한 요청이 있을 때까지 정지
    // 4. 취소 가능 - 이 flow을 수집하는 범위가 취소되면 Room은 이 쿼리 관찰을 취소
    @Query("SELECT * FROM plants ORDER BY name")
    fun getPlantsFlow(): Flow<List<Plant>>

    @Query("SELECT * FROM plants WHERE growZoneNumber = :growZoneNumber ORDER BY name")
    fun getPlantsFlowWithGrowZoneNumber(growZoneNumber: Int): Flow<List<Plant>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(plants: List<Plant>)
}
