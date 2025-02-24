package com.example.android.sports

import android.graphics.drawable.Icon
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.magnifier
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource

// 네비게이션 컴포저블 정의
@RequiresApi(Build.VERSION_CODES.M)
@Composable
fun BottomNavigationBar(
    menuItem: List<MenuItem>,
    modifier: Modifier = Modifier,
    // 네비게이션 클릭시 불릭 콜백
    onMenuSelected: (MenuItem) -> Unit = {}
) {
    NavigationBar (
        modifier = modifier
    ){
        menuItem.forEach { menuItem ->
            NavigationBarItem(
                selected = false,
                onClick = { onMenuSelected(menuItem) },
                icon = {
                    Icon(
                        painter = painterResource(menuItem.iconId),
                        contentDescription = null
                    )
                },
                label = { Text(text = stringResource(id = menuItem.labelId)) }
            )
        }
    }
}
