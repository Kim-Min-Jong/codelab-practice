/*
 * Copyright 2024 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.cars.roadreels

import android.os.Build
import android.os.Bundle
import android.view.WindowInsetsController
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.android.cars.roadreels.ui.theme.RoadReelsTheme
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat.OnControllableInsetsChangedListener



class MainActivity : ComponentActivity() {
    private lateinit var onControllableInsetsChangedListener: OnControllableInsetsChangedListener

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        // Override the layoutInDisplayCutoutMode set in enableEdgeToEdge()
        window.updateAttributes {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                layoutInDisplayCutoutMode = resources.getInteger(R.integer.windowLayoutInDisplayCutoutMode)
            }
        }

        setContent {
            // 변경사항을 수신 대기하도록 OnControllableInsetsChangedListener를 설정
            var controllableInsetsTypeMask by remember { mutableStateOf(DEFAULT_CONTROLLABLE_INSETS) }

            onControllableInsetsChangedListener = OnControllableInsetsChangedListener { _, typeMask ->
                if (controllableInsetsTypeMask != typeMask) {
                    controllableInsetsTypeMask = typeMask
                }
            }

            WindowCompat.getInsetsController(window, window.decorView)
                .addOnControllableInsetsChangedListener(onControllableInsetsChangedListener)

            // 테마와 앱 컴포저블을 포함하고 값을 LocalControllableInsets에 결합하는 최상위 수준 CompositionLocalProvider를 추가
            CompositionLocalProvider(value = LocalControllableInsets provides controllableInsetsTypeMask) {
                RoadReelsTheme {
                    RoadReelsApp(calculateWindowSizeClass(this))
                }
            }
        }
    }
}
