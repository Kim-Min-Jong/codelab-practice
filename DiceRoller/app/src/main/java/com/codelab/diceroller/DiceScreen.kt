package com.codelab.diceroller

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun DiceRollerApp() {
    // 인자로 다시 주는 이유
    // 리컴포지션 때문에
    // 리컴포지션시 기본 인자가 다시 생성되므로 비효율적이여서 인자로 제공
    DiceWithButtonAndImage(
        // 가득 채우되
        modifier = Modifier.fillMaxSize()
        // Alignment를 이용해서 공간내 중앙 정렬
            .wrapContentSize(Alignment.Center)
    )
}

@Composable
fun DiceWithButtonAndImage(
    modifier: Modifier = Modifier
) {

}
