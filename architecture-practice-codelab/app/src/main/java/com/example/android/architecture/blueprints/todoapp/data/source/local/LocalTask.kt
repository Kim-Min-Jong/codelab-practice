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

package com.example.android.architecture.blueprints.todoapp.data.source.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.android.architecture.blueprints.todoapp.data.Task

// data 영역의 데이터베이스 데이터모델
// 각자 영역에 맞는 데이터 모델만을 맡기 위해 생성

@Entity(tableName = "task")
data class LocalTask(
    @PrimaryKey val id: String,
    var title: String,
    var description: String,
    var isCompleted: Boolean
)

/**
 * 함수 매핑은 함수가 사용되는 경계에 있어야 합니다. 여기서는 LocalTask.kt가 해당 유형과 함수를 매핑하기에 좋은 위치입니다.
 */
// repository 단에서 사용할 mapper 함수

// LocalTask to Task
fun LocalTask.toExternal() = Task(
    id = id,
    title = title,
    description = description,
    isCompleted = isCompleted
)

// for list
fun List<LocalTask>.toExternal() = this.map { it.toExternal() }

fun Task.toLocal() = LocalTask(
    id = id,
    title = title,
    description = description,
    isCompleted = isCompleted
)
