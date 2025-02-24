package com.example.android.sports

import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes

// 뷰 시스템으로 되어있는 네비게이셔늘 컴포즈로 변환하기위한
// 메뉴 아이템 클래스
sealed class MenuItem(
    @DrawableRes val iconId: Int,
    @StringRes val labelId: Int,
    @IdRes val destinationId: Int,
) {
    object Home: MenuItem(
        R.drawable.ic_baseline_home_24,
        R.string.home,
        R.id.SportsListFragment
    )

    object Favorites: MenuItem(
        R.drawable.ic_baseline_favorite_24,
        R.string.favorites,
        R.id.FavoritesFragment
    )

    object Settings: MenuItem(
        R.drawable.ic_baseline_settings_24,
        R.string.settings,
        R.id.SettingsFragment
    )
}
