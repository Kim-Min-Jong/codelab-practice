package com.codelab.prac3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.codelab.prac3.ui.theme.Prac3Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Prac3Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WellnessScreen()
                }
            }
        }
    }
}

// 하루 동안 마신 물잔 개수를 계산하는 워터 카운터 컴포저블
@Composable
fun WaterCounter(
    modifier: Modifier = Modifier
) {
    val count = 0
    Text(
        text = "You've had $count glasses.",
        modifier = modifier.padding(16.dp)
    )
}

// 워터 카운터를 호출하는 기본 컴포저블
@Composable
fun WellnessScreen(
    modifier: Modifier = Modifier
) {
    WaterCounter(modifier)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Prac3Theme {
        WellnessScreen()
    }
}
