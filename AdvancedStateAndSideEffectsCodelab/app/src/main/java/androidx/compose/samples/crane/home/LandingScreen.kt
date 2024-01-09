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

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.samples.crane.R
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import kotlinx.coroutines.delay

private const val SplashWaitTime: Long = 2000

// 스플래시 화면을 만드는 컴포저블
@Composable
fun LandingScreen(onTimeout: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        // Codelab: LaunchedEffect and rememberUpdatedState step
        // Make LandingScreen disappear after loading data
        // compose는 UI레이어 내에서 코루틴을 안전하게 실행하는 API를 제공함

        // compose의 부작용 중 하나로, 컴포저블 함수 밖에서 발생하는 앱 상태의 관한 변경사항이 있다.
        // 스플래시 화면 표시/숨기기를 위해 상태 변경하는 것은 onTimeout 콜백에서 발생하고, onTimeout 호출 전에 코루틴을 사용하여 항목을 로드해야 하므로 코루틴의 컨텍스트에서 상태 변경이 발생해야 한다.
        // 컴포저블 내에서 안전하게 정지 함수를 호출하려면 Compose에서 코루틴 범위의 부작용을 트리거하는 LaunchedEffect API를 사용한다.

        // LaunchedEffect가 컴포지션을 시작하면 매개변수로 전달된 코드 블록으로 코루틴이 실행된다.
        // LaunchedEffect가 컴포지션을 종료하면 코루틴이 취소된다.
//        LaunchedEffect(onTimeout) {
//            delay(SplashWaitTime)
//            // 다시 시작하면서  delay가 다시 호출되는 현상이 발생한다.
//            onTimeout
//        }

        // 컴포저블 수명 주기 동안 한번만 트러거 시켜주면 된다.
        // 마지막 onTimeOut만 호출하도록 하려면
        // rememberUpdatedState API를 사용하여 onTimeout을 저장합니다. 이 API는 다음과 같이 최신 값을 캡처하고 업데이트 한다.
        val currentOnTimeOut by rememberUpdatedState(newValue = onTimeout)
        LaunchedEffect(key1 = Unit) {
            delay(SplashWaitTime)
            currentOnTimeOut()
        }

        Image(painterResource(id = R.drawable.ic_crane_drawer), contentDescription = null)
    }
}
