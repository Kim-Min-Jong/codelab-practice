package com.codelab.diceroller

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun DiceRollerApp() {
    // 인자로 다시 주는 이유
    // 리컴포지션 때문에
    // 리컴포지션시 기본 인자가 다시 생성되므로 비효율적이여서 인자로 제공
    DiceWithButtonAndImage(
        // 가득 채우되
        modifier = Modifier
            .fillMaxSize()
            // Alignment를 이용해서 공간내 중앙 정렬
            .wrapContentSize(Alignment.Center)
    )
}

@Composable
fun DiceWithButtonAndImage(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.dice_1),
            contentDescription = "1"
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                
            }
        ) {
            Text(text = stringResource(id = R.string.roll))
        }
    }
}
