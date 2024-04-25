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

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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

}

sealed class Screens(val route: String) {
    object Home : Screens("home")
    object AddLog : Screens("add_log")
    object Camera : Screens("camera")
}
