/*
 * Copyright 2019 The Android Open Source Project
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

package com.example.android.architecture.blueprints.todoapp.data

import com.example.android.architecture.blueprints.todoapp.data.source.local.FakeTaskDao
import com.example.android.architecture.blueprints.todoapp.data.source.local.LocalTask
import com.example.android.architecture.blueprints.todoapp.data.source.network.TaskNetworkDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultTaskRepositoryTest {
    private var testDispatcher = UnconfinedTestDispatcher()
    private var testScope = TestScope(testDispatcher)

    private val localTasks = listOf(
        LocalTask(id = "1", title = "title1", description = "description1", isCompleted = false),
        LocalTask(id = "2", title = "title2", description = "description2", isCompleted = true),
    )

    private val localDataSource = FakeTaskDao(localTasks)
    private val networkDataSource = TaskNetworkDataSource()
    private val taskRepository = DefaultTaskRepository(
        localDataSource = localDataSource,
        remoteDataSource = networkDataSource,
        dispatcher = testDispatcher,
        scope = testScope
    )
}
