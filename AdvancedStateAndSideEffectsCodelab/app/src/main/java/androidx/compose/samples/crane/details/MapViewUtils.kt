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

package androidx.compose.samples.crane.details

import android.os.Bundle
import androidx.annotation.FloatRange
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.samples.crane.R
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.android.libraries.maps.GoogleMap
import com.google.android.libraries.maps.MapView
import java.lang.IllegalStateException

/**
 * Remembers a MapView and gives it the lifecycle of the current LifecycleOwner
 */
@Composable
fun rememberMapViewWithLifecycle(): MapView {
    // 현재 상태에서 실행은 되지만 MapView가 수명주기를 따르지 않아 문제가 발생할 수 있다.
    val context = LocalContext.current
    // Codelab: DisposableEffect step. Make MapView follow the lifecycle
    return remember {
        // MapView는 컴포저블이 아닌 뷰이므로 액티비티의 수명 주기를 따르는 것이 좋다.
        // 수명 주기 이벤트 수신을 대기하고 올바른 호출을 위해 LifecycleEventObserver를 만들어 준다.
        // 그 후 이 ㅇ옵저버를 액티비티에 수명주기에 추가한다.
        MapView(context).apply {
            id = R.id.map
            onCreate(Bundle())
        }
    }
}

// MapView의 수명주기를 관찰하는 옵저버
private fun getMapLifecycleObserver(mapView: MapView): LifecycleEventObserver =
    LifecycleEventObserver { source, event ->
        when(event){
            Lifecycle.Event.ON_CREATE -> mapView.onCreate(Bundle())
            Lifecycle.Event.ON_START -> mapView.onStart()
            Lifecycle.Event.ON_RESUME -> mapView.onResume()
            Lifecycle.Event.ON_PAUSE -> mapView.onPause()
            Lifecycle.Event.ON_STOP -> mapView.onStop()
            Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
            else -> throw IllegalStateException()
        }
    }
fun GoogleMap.setZoom(
    @FloatRange(from = MinZoom.toDouble(), to = MaxZoom.toDouble()) zoom: Float
) {
    resetMinMaxZoomPreference()
    setMinZoomPreference(zoom)
    setMaxZoomPreference(zoom)
}
