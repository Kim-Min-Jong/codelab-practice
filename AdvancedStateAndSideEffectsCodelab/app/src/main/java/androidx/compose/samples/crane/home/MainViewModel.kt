/*
 * Copyright 2020 The Android Open Source Project
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

package androidx.compose.samples.crane.home

import androidx.compose.samples.crane.data.DestinationsRepository
import androidx.compose.samples.crane.data.ExploreModel
import androidx.compose.samples.crane.di.DefaultDispatcher
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

const val MAX_PEOPLE = 4

@HiltViewModel
class MainViewModel @Inject constructor(
    private val destinationsRepository: DestinationsRepository,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) : ViewModel() {

    val hotels: List<ExploreModel> = destinationsRepository.hotels
    val restaurants: List<ExploreModel> = destinationsRepository.restaurants

    //  앱을 실행할 때 확인할 수 있듯이 항공편 목적지 목록이 비어 있다.
    // 다음 두 단계를 완료하여 이 문제를 해결할 수 있습니다.
    //
    // ViewModel에 로직을 추가하여 UI 상태를 생성. 이 경우 UI 상태는 추천 목적지 목록.
    // UI에서 UI 상태를 사용하면 화면에 UI가 표시.
    // UI 상태를 생성하는 데 사용할 수 있는 API가 몇 가지 있다.
    // 이 방법 외에 다른 방법은 상태 프로덕션 파이프라인의 출력 유형 문서에 요약되어 있다.
    // 일반적으로 Kotlin의 StateFlow를 사용하여 UI 상태를 생성하는 것이 좋습니다.

    // 변수 캡슐화
    private val _suggestedDestination = MutableStateFlow<List<ExploreModel>>(emptyList())
    val suggestedDestinations: StateFlow<List<ExploreModel>> = _suggestedDestination.asStateFlow()

    // 뷰모델이 처음 호출 될 때, State의 변경
    init {
        // TODO: UI 단에 데이터를 표시 해주어야함
        _suggestedDestination.value = destinationsRepository.destinations
    }

    fun updatePeople(people: Int) {
        viewModelScope.launch {
            if (people > MAX_PEOPLE) {
            // TODO Codelab: Uncomment
            //  _suggestedDestinations.value = emptyList()
            } else {
                val newDestinations = withContext(defaultDispatcher) {
                    destinationsRepository.destinations
                        .shuffled(Random(people * (1..100).shuffled().first()))
                }
                // TODO Codelab: Uncomment
                //  _suggestedDestinations.value = newDestinations
            }
        }
    }

    fun toDestinationChanged(newDestination: String) {
        viewModelScope.launch {
            val newDestinations = withContext(defaultDispatcher) {
                destinationsRepository.destinations
                    .filter { it.city.nameToDisplay.contains(newDestination) }
            }
            // TODO Codelab: Uncomment
            //  _suggestedDestinations.value = newDestinations
        }
    }
}
