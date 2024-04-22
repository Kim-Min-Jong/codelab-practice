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

import com.example.android.architecture.blueprints.todoapp.data.source.local.TaskDao
import com.example.android.architecture.blueprints.todoapp.data.source.local.toExternal
import com.example.android.architecture.blueprints.todoapp.data.source.local.toLocal
import com.example.android.architecture.blueprints.todoapp.data.source.network.TaskNetworkDataSource
import com.example.android.architecture.blueprints.todoapp.data.source.network.toLocal
import com.example.android.architecture.blueprints.todoapp.data.source.network.toNetwork
import com.example.android.architecture.blueprints.todoapp.di.DefaultDispatcher
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class DefaultTaskRepository @Inject constructor(
    // inject를 통해 주입 됨
    private val localDataSource: TaskDao,
    private val remoteDataSource: TaskNetworkDataSource,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher
) {
    fun observeAll(): Flow<List<Task>> = localDataSource.observeAll().map { tasks ->
        tasks.toExternal()
    }

    // 새로운 task를 만들고 id를 반환하는 메소드
    suspend fun create(title: String, description: String): String {
        val taskId = withContext(dispatcher) {
            UUID.randomUUID().toString()
        }

        val task = Task(
            id = taskId,
            title = title,
            description = description
        )

        localDataSource.upsert(task.toLocal())
        saveTasksToNetwork()
        return taskId
    }

    // 완료를 알리는 메소드
    suspend fun complete(taskId: String) {
        localDataSource.updateCompleted(taskId, true)
        saveTasksToNetwork()
    }

    // refresh를 하는 메소드 (로컬을 네트워크 데이터로 대체)
    suspend fun refresh() {
        val networkTasks = remoteDataSource.loadTasks()
        localDataSource.deleteAll()
        val localTasks = withContext(dispatcher) {
            networkTasks.toLocal()
        }
        localDataSource.upsertAll(localTasks)
    }

    private suspend fun saveTasksToNetwork() {
        val localTasks = localDataSource.observeAll().first()
        val networkTasks = withContext(dispatcher) {
            localTasks.toNetwork()
        }
        remoteDataSource.saveTasks(networkTasks)
    }
}
