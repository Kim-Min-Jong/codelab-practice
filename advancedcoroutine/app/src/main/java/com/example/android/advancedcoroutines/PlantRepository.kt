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

import androidx.annotation.AnyThread
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import com.example.android.advancedcoroutines.utils.CacheOnSuccess
import com.example.android.advancedcoroutines.utils.ComparablePair
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.switchMap
import kotlinx.coroutines.withContext

/**
 * Repository module for handling data operations.
 *
 * This PlantRepository exposes two UI-observable database queries [plants] and
 * [getPlantsWithGrowZone].
 *
 * To update the plants cache, call [tryUpdateRecentPlantsForGrowZoneCache] or
 * [tryUpdateRecentPlantsCache].
 */
class PlantRepository private constructor(
    private val plantDao: PlantDao,
    private val plantService: NetworkService,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) {

    /**
     * Fetch a list of [Plant]s from the database.
     * Returns a LiveData-wrapped List of Plants.
     */
//    val plants = liveData<List<Plant>> {
//        val plantsLiveData = plantDao.getPlants()
//
//        // suspend 함수를 통해 불러오는 도중 실패하면 전체가 실패하도록 -> 안정성, 보안성
//        val customSortOrder = plantsListSortOrderCache.getOrAwait()
//        // livedata에 값을 보냄 (emitSource)
//        // 호출할 때마다 이전 값이 사라지고 새로 생성
//        emitSource(plantsLiveData.map {
//            plants -> plants.applySort(customSortOrder)
//        })
//    }
    // livedata -> flow
    val plantsFlow: Flow<List<Plant>>
        get() = plantDao.getPlantsFlow()


    // 정렬 순서를 가져와 메모리에 캐시하는 함수
    // 에러가 생길 경우 빈 리스트를 반환
    private var plantsListSortOrderCache =
        CacheOnSuccess(onErrorFallback = { listOf<String>()}) {
            plantService.customPlantSortOrder()
        }

    // 리스트를 다시 정렬해 customSortOrder에 있는 Plants를 목록 앞부분에 배치
    private fun List<Plant>.applySort(customOrder: List<String>): List<Plant> =
        sortedBy { plant ->
            val positionForItem = customOrder.indexOf(plant.plantId).let { order ->
                if (order > -1) order else Int.MAX_VALUE
            }
            ComparablePair(positionForItem, plant.name)
        }

    // 복잡한 livedata 비동기 처리
    // 각 값이 처리될 떄 suspend transform 구현
    // 우선 기본 쓰레드에서 안전하게 사용할 수 있는 범용 정렬 알고리즘 구현
    @AnyThread
    suspend fun List<Plant>.applyMainSafeSort(customSortOrder: List<String>) =
        // dispatcher 전환
        // withContext를 호출하면 람다 전용의 다른 디스패처로 전환되었다가 이 람다 결과와 함께 호출했던 디스패처로 돌아옴
        withContext(defaultDispatcher) {
            this@applyMainSafeSort.applySort(customSortOrder)
        }

    /**
     * Fetch a list of [Plant]s from the database that matches a given [GrowZone].
     * Returns a LiveData-wrapped List of Plants.
     */
    fun getPlantsWithGrowZone(growZone: GrowZone) =
        // 네트워크에서 맞춤 정렬 순서가 수신되면 이 순서를 새 기본 안전 applyMainSafeSort와 함께 사용
        // 여기도 중간에 실패하면 모두 실패하여 종료하도록 -- 캐시되있으므로 데이터는 안전함
//        plantDao.getPlantsWithGrowZoneNumber(growZone.number)
//            .switchMap { plantsList ->
//                liveData {
//                    val customSortOrder = plantsListSortOrderCache.getOrAwait()
//                    emit(plantsList.applyMainSafeSort(customSortOrder))
//                }
//            }
        // livedata -> flow
        plantDao.getPlantsFlowWithGrowZoneNumber(growZone.number)

    /**
     * Returns true if we should make a network request.
     */
    private fun shouldUpdatePlantsCache(): Boolean {
        // suspending function, so you can e.g. check the status of the database here
        return true
    }

    /**
     * Update the plants cache.
     *
     * This function may decide to avoid making a network requests on every call based on a
     * cache-invalidation policy.
     */
    suspend fun tryUpdateRecentPlantsCache() {
        if (shouldUpdatePlantsCache()) fetchRecentPlants()
    }

    /**
     * Update the plants cache for a specific grow zone.
     *
     * This function may decide to avoid making a network requests on every call based on a
     * cache-invalidation policy.
     */
    suspend fun tryUpdateRecentPlantsForGrowZoneCache(growZoneNumber: GrowZone) {
        if (shouldUpdatePlantsCache()) fetchPlantsForGrowZone(growZoneNumber)
    }

    /**
     * Fetch a new list of plants from the network, and append them to [plantDao]
     */
    private suspend fun fetchRecentPlants() {
        val plants = plantService.allPlants()
        plantDao.insertAll(plants)
    }

    /**
     * Fetch a list of plants for a grow zone from the network, and append them to [plantDao]
     */
    private suspend fun fetchPlantsForGrowZone(growZone: GrowZone) {
        val plants = plantService.plantsByGrowZone(growZone)
        plantDao.insertAll(plants)
    }



    companion object {

        // For Singleton instantiation
        @Volatile private var instance: PlantRepository? = null

        fun getInstance(plantDao: PlantDao, plantService: NetworkService) =
            instance ?: synchronized(this) {
                instance ?: PlantRepository(plantDao, plantService).also { instance = it }
            }
    }
}
