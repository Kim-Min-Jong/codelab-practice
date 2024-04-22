/*
 * Copyright 2023 The Android Open Source Project
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

package com.example.android.architecture.blueprints.todoapp.data.source.network

import com.example.android.architecture.blueprints.todoapp.data.source.local.LocalTask

// data 영역의 네트워크 데이터모델
// 각자 영역에 맞는 데이터 모델만을 맡기 위해 생성

data class NetworkTask(
    val id: String,
    val title: String,
    val shortDescription: String,
    val priority: Int? = null,
    val status: TaskStatus = TaskStatus.ACTIVE
) {
    enum class TaskStatus {
        ACTIVE,
        COMPLETE
    }
}

// Network 영역 mapper 함수

// network to local
fun NetworkTask.toLocal() = LocalTask(
    id = id,
    title = title,
    description = shortDescription,
    isCompleted = (status == NetworkTask.TaskStatus.COMPLETE)
)
fun List<NetworkTask>.toLocal() = this.map { it.toLocal() }

// local to network
fun LocalTask.toNetwork() = NetworkTask(
    id = id,
    title = title,
    shortDescription = description,
    status = if (isCompleted) NetworkTask.TaskStatus.COMPLETE else NetworkTask.TaskStatus.ACTIVE
)
fun List<LocalTask>.toNetwork() = this.map { it.toNetwork() }

