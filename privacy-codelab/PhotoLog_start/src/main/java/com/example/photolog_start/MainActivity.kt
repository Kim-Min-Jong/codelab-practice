/*
 * Copyright (C) 2022 The Android Open Source Project
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
/*
 * 앱이 권한을 요청하는 방식을 살펴보고 권한 모델이 최적이 아니면 발생할 수 있는 현상
 *
 * 1. 실행 직후 사용자에게는 여러 권한을 부여하도록 요청하는 권한 메시지가 즉시 표시됩니다. 이는 사용자에게 혼란을 야기할 수 있으며 앱에 대한 신뢰를 잃거나 최악의 경우 앱을 제거할 수 있습니다.
 * 2. 앱은 모든 권한이 부여될 때까지 사용자가 계속 진행하도록 허용하지 않습니다. 사용자는 실행 시 이러한 모든 민감한 정보에 대한 액세스 권한을 부여할 정도로 앱을 신뢰하지 않을 수 있습니다.
 */

package com.example.photolog_start

import android.app.AppOpsManager
import android.app.AppOpsManager.OPSTR_CAMERA
import android.app.AppOpsManager.OPSTR_COARSE_LOCATION
import android.app.AsyncNotedAppOp
import android.app.SyncNotedAppOp
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.photolog_start.ui.theme.PhotoLogTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    lateinit var permissionManager: PermissionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionManager = (application as PhotoLogApplication).permissions

        // TODO: Step 2. Register Data Access Audit Callback
        // 로깅 매니저 등록 (안드로이드 R이상)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val appOpsManager = getSystemService(AppOpsManager::class.java) as AppOpsManager
            appOpsManager.setOnOpNotedCallback(mainExecutor, DataAccessAuditListener)
        }

        setContent {
            PhotoLogTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    // 사용자가 홈페이지로 이동하기 전에 모든 권한을 승인하도록 강제하는 권한 화면 -> 삭제 요망
//                    val startNavigation =
//                        if (permissionManager.hasAllPermissions) {
//                            Screens.Home.route
//                        } else {
//                            Screens.Permissions.route
//                        }

                    // 권한 요청을 시작하지 않아도 앱을 사용할 수 있도록
                    val startNavigation = Screens.Home.route

                    NavHost(navController = navController, startDestination = startNavigation) {
                         //권한 화면이 더 이상 필요하지 않으므로 삭제
//                        composable(Screens.Permissions.route) { PermissionScreen(navController) }
                        composable(Screens.Home.route) { HomeScreen(navController) }
                        composable(Screens.AddLog.route) { AddLogScreen(navController) }
                        composable(Screens.Camera.route) { CameraScreen(navController) }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            permissionManager.checkPermissions()
        }
    }

    // TODO: Step 1. Create Data Access Audit Listener Object
    // 사용자가 접근한 데이터에 대해 로깅을 하기 위한 오브젝트

    /*
     데이터 액세스 분석 API
    onNoted(): 앱이 사용자 데이터에 액세스하는 동기(양방향 바인딩) API를 호출할 때 호출됩니다. 이는 일반적으로 콜백이 필요 없는 API 호출입니다.
    onAsyncNoted(): 앱이 사용자 데이터에 액세스하는 비동기(단방향 바인딩) API를 호출할 때 호출됩니다. 이는 일반적으로 콜백이 필요한 API 호출이며 콜백이 호출될 때 데이터 액세스가 발생합니다.
    onSelfNoted(): 호출 가능성이 매우 낮습니다. 예를 들어 앱이 자체 UID를 noteOp()에 전달할 때 발생합니다.
     */
    @RequiresApi(Build.VERSION_CODES.R)
    object DataAccessAuditListener: AppOpsManager.OnOpNotedCallback() {
        override fun onNoted(op: SyncNotedAppOp) {
            android.util.Log.d("DataAccessAuditListener","Sync Private Data Accessed: ${op.op}")
        }

        override fun onSelfNoted(op: SyncNotedAppOp) {
            android.util.Log.d("DataAccessAuditListener","Self Private Data accessed: ${op.op}")
        }

        @RequiresApi(Build.VERSION_CODES.R)
        override fun onAsyncNoted(asyncOp: AsyncNotedAppOp) {
            var emoji = when (asyncOp.op) {
                OPSTR_COARSE_LOCATION -> "\uD83D\uDDFA"
                OPSTR_CAMERA -> "\uD83D\uDCF8"
                else -> "?"
            }

            android.util.Log.d("DataAccessAuditListener", "Async Private Data ($emoji) Accessed:${asyncOp.op}")
        }

    }
}

sealed class Screens(val route: String) {
    object Home : Screens("home")
    object AddLog : Screens("add_log")
    object Camera : Screens("camera")
}
