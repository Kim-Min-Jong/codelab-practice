/*
 * Copyright 2021 The Android Open Source Project
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

package androidx.compose.samples.crane.base

import androidx.annotation.DrawableRes
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.samples.crane.ui.captionTextStyle
import androidx.compose.ui.graphics.SolidColor

@Composable
fun CraneEditableUserInput(
    hint: String,
    caption: String? = null,
    @DrawableRes vectorImageId: Int? = null,
    onInputChanged: (String) -> Unit
) {
    // Codelab: Encapsulate this state in a state holder
    // 컴포저블 내부에 상태가 있을 경우 TextField 값은 끌어올려지지 않아 외부에서 제어할 수 없으므로 테스트가 더 어렵다.
    // 또한, 이 컴포저블의 논리가 더 복잡해지고 내부 상태가 더 쉽게 동기화되지 않을 수 있다.
    var textState by remember { mutableStateOf(hint) }
    val isHint = { textState == hint }

    CraneBaseUserInput(
        caption = caption,
        tintIcon = { !isHint() },
        showCaption = { !isHint() },
        vectorImageId = vectorImageId
    ) {
        BasicTextField(
            value = textState,
            onValueChange = {
                textState = it
                if (!isHint()) onInputChanged(textState)
            },
            textStyle = if (isHint()) {
                captionTextStyle.copy(color = LocalContentColor.current)
            } else {
                MaterialTheme.typography.body1.copy(color = LocalContentColor.current)
            },
            cursorBrush = SolidColor(LocalContentColor.current)
        )
    }
}


/**
 * 내부 상태를 담당하는 상태 홀더를 만들어 모든 상태 변경사항을 한 곳으로 중앙화할 수 있다.
 * 이렇게 하면 상태가 쉽게 동기화되고 관련 로직도 모두 단일 클래스로 그룹화된다.
 * 또한 이 상태는 쉽게 끌어올릴 수 있으며 이 컴포저블의 호출자로부터 사용될 수 있다.
 */
class EditableUserInputState(
    private val hint: String,
    initialText: String
) {
    // CraneEditableUserInput에서와 같이 변경가능한 String State
    // 상태 관리를 위해 사용
    // 캡슐화를 위해 private set을 적용
    var text by mutableStateOf(initialText)
        private set

    // text 변수에 접근을 위해 메소드를 통해 접근
    fun updateText(newText: String) {
        text = newText
    }

    // text 가 힌트인지 확인하는 변수
    val isHint: Boolean
        get() = text == hint

    // 로직이 추가되면 이 상태 관리하는 클래스에서만 변경을 하면 변경에 용이함
}

// 상태 홀더 기억하기
// 상태 홀더가 항상 기억되어야 컴포지션에서 유지되고, 매번 만들 필요가없음
// 그러기 위해 기억하는 객체를 만들 필요가 있음
@Composable
fun rememberEditableUserInputState(hint: String): EditableUserInputState =
    remember(hint) {
        EditableUserInputState(hint, hint)
    }
