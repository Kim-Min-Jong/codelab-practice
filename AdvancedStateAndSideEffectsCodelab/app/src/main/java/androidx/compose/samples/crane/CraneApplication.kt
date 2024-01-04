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

package androidx.compose.samples.crane

import android.app.Application
import androidx.compose.samples.crane.util.UnsplashSizingInterceptor
import coil.ImageLoader
import coil.ImageLoaderFactory
import dagger.hilt.android.HiltAndroidApp

/*
    Jetpack Compose의 고급 상태 및 부작용

    Jetpack Compose의 상태 및 부작용 API와 관련된 고급 개념을 알아봅니다.
    로직이 자명하지 않은 스테이트풀(Stateful) 컴포저블의 상태 홀더를 만드는 방법,
    Compose 코드에서 코루틴을 만들고 정지 함수를 호출하는 방법,
    다양한 사용 사례를 달성하기 위해 부작용을 트리거하는 방법을 살펴보겠습니다.
 */

@HiltAndroidApp
class CraneApplication : Application(), ImageLoaderFactory {

    /**
     * Create the singleton [ImageLoader].
     * This is used by [rememberImagePainter] to load images in the app.
     */
    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .components {
                add(UnsplashSizingInterceptor)
            }
            .build()
    }
}
