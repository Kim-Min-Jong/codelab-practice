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

package com.example.android.cars.roadreels.ui.screen.player

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.android.cars.roadreels.LocalControllableInsets
import com.example.android.cars.roadreels.SupportedOrientation
import com.example.android.cars.roadreels.supportedOrientations
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

const val VIDEO_URI =
    "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"

data class PlayerState(
    val isLoading: Boolean = true,
    val isPlaying: Boolean = false,
    val durationMillis: Long,
    val currentPositionMillis: Long,
    val mediaMetadata: MediaMetadata
)

fun Player.toPlayerState(): PlayerState {
    return PlayerState(
        isLoading,
        isPlaying,
        duration,
        currentPosition,
        mediaMetadata,
    )
}

@Composable
fun PlayerScreen(
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current as Activity
    val coroutineScope = rememberCoroutineScope()

    val player = remember(context) { ExoPlayer.Builder(context).build() }
    var isShowingControls by remember { mutableStateOf(true) }
    var playerState by remember(player) { mutableStateOf(player.toPlayerState()) }

    // 수명 주기에 따른 플레이어 재생상태 컨트롤
    LifecycleEventEffect(Lifecycle.Event.ON_PAUSE) {
        player.pause()
    }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        player.play()
    }


    DisposableEffect(player) {
        val listener = object : Player.Listener {
            override fun onEvents(player: Player, events: Player.Events) {
                super.onEvents(player, events)

                if (events.contains(Player.EVENT_MEDIA_METADATA_CHANGED)
                    || events.contains(Player.EVENT_IS_LOADING_CHANGED)
                    || events.contains(Player.EVENT_IS_PLAYING_CHANGED)
                ) {
                    playerState = player.toPlayerState()
                }
            }
        }

        player.addListener(listener)

        onDispose {
            player.removeListener(listener)
            player.release()
        }
    }

    // Continually update to capture the current position
    LaunchedEffect(player) {
        while (true) {
            playerState = player.toPlayerState()
            delay(1000)
        }
    }

    val mediaSource =
        MediaItem.fromUri(VIDEO_URI)
            .buildUpon().setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle("Big Buck Bunny")
                    .setArtworkUri("https://peach.blender.org/wp-content/uploads/title_anouncement.jpg".toUri())
                    .build()
            ).build()

    // When either the player or the mediaSource change, react to that change
    LaunchedEffect(player, mediaSource) {
        player.setMediaItem(mediaSource)
        player.prepare()
        player.playWhenReady = true
    }

    val windowInsetsController = remember(context) {
        WindowCompat.getInsetsController(
            context.window,
            context.window.decorView
        )
    }

    LaunchedEffect(Unit) {
        context.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        // 요청된 방향을 설정하도록 호출,
        //모바일 기기의 멀티 윈도우 모드에서도 앱이 이와 비슷한 문제에 직면할 수 있으므로 이 경우에도 방향이 동적으로 설정되지 않도록 하는 검사를 포함
        if (context.supportedOrientations().contains(SupportedOrientation.LandScape)
            && !context.isInMultiWindowMode
        ) {
            context.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        }

        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }

    DisposableEffect(isShowingControls, playerState.isPlaying) {
        val coroutine = coroutineScope.launch {
            if (isShowingControls) {
                delay(5000)
                isShowingControls = false
            }
        }

        onDispose {
            coroutine.cancel()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            player.release()
            player.clearMediaItems()

            // Reset the requested orientation to the default
            context.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

            windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
            windowInsetsController.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
    )


    //플레이어에서 현재 값을 읽고 이를 사용하여 패딩에 사용할 인셋을 확인
    val controllableInsetsTypeMask = LocalControllableInsets.current

    var windowInsetsForPadding = WindowInsets(0.dp)
    if (controllableInsetsTypeMask.and(WindowInsetsCompat.Type.statusBars()) == 0) {
        windowInsetsForPadding = windowInsetsForPadding.union(WindowInsets.statusBars)
    }
    if (controllableInsetsTypeMask.and(WindowInsetsCompat.Type.navigationBars()) == 0) {
        windowInsetsForPadding = windowInsetsForPadding.union(WindowInsets.navigationBars)
    }

    // 앞에서 적용한 변경사항은 앱이 깜박이는 루프에 진입하거나 레터박스가 발생하지 않도록 하지만 시스템 표시줄은 항상 숨길 수 있다는 문제 발생
    // 자동차를 사용하는 사용자는 휴대전화나 태블릿을 사용할 때와 다른 요구사항을 갖기 때문에 OEM은 화면에서 항상 실내 온도 조절기와 같은 차량 컨트롤에 액세스할 수 있도록 앱이 시스템 표시줄을 숨기지 못하도록 할 수 있음
    // 그렇기 떄문에 시스템 바가 없으면 앱 탐색이 불가해 계속 보여줘야함

    Box(
        modifier =
        Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {

        PlayerView(
            player,
            modifier = Modifier
                .fillMaxSize()
                .clickable { isShowingControls = !isShowingControls }
        )

        PlayerControls(
            modifier = Modifier
                .fillMaxSize(),
            visible = isShowingControls,
            playerState = playerState,
            onPlayPause = { if (playerState.isPlaying) player.pause() else player.play() },
            onSeek = { player.seekTo(it) },
            onClose = onClose
        )
    }
}
