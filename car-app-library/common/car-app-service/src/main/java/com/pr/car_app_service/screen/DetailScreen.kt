package com.pr.car_app_service.screen

import android.graphics.Color
import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.ActionStrip
import androidx.car.app.model.CarColor
import androidx.car.app.model.CarIcon
import androidx.car.app.model.MessageTemplate
import androidx.car.app.model.Pane
import androidx.car.app.model.PaneTemplate
import androidx.car.app.model.Row
import androidx.car.app.model.Template
import androidx.core.graphics.drawable.IconCompat
import com.example.places.data.PlacesRepository
import com.example.places.data.model.toIntent
import com.pr.car_app_service.R

class DetailScreen(
    carContext: CarContext,
    private val placeId: Int
) : Screen(carContext) {
    // 상태 추가
    private var isFavorite = false
    override fun onGetTemplate(): Template {
        // 장소 가져옴
        val place = PlacesRepository().getPlace(placeId)
            ?: return MessageTemplate.Builder("Place not found")
                .setHeaderAction(Action.BACK)
                .build()

        val navigationAction = Action.Builder()
            .setTitle("Navigate")
            .setIcon(
                CarIcon.Builder(
                    IconCompat.createWithResource(
                        carContext,
                        R.drawable.baseline_navigation_24
                    )
                ).build()
            )
            // 클릭 시 네비게이션 앱을 보여줌
            .setOnClickListener {
                carContext.startCarApp(place.toIntent(CarContext.ACTION_NAVIGATE))
            }.build()

        // 이 컴포넌트는 타이틀 반대편 헤더 행에 놓는 것이 좋고 주요액션이 아니여야 한다.
        // 주요액션이 아니기 때문에 action이 아닌 actionStrip을 통해 스크린에 보여준다.
        val actionStrip = ActionStrip.Builder()
            .addAction(
                Action.Builder()
                    .setIcon(
                        CarIcon.Builder(
                            IconCompat.createWithResource(
                                carContext,
                                R.drawable.baseline_favorite_24
                            )
                        ).setTint(
                            if (isFavorite) CarColor.RED else CarColor.createCustom(
                                Color.LTGRAY,
                                Color.DKGRAY
                            )

                        ).build()
                    )
                    .setOnClickListener{
                        // 상태를 바꾼후
                        isFavorite = !isFavorite
                        // 다시 그림
                        invalidate()
                    }.build()
            ).build()

        return PaneTemplate.Builder(
            Pane.Builder()
                .addAction(navigationAction)
                .addRow(
                    Row.Builder()
                        .setTitle("Coordinates")
                        .addText("${place.latitude}, ${place.longitude}")
                        .build()
                ).addRow(
                    Row.Builder()
                        .setTitle("Description")
                        .addText(place.description)
                        .build()
                ).build()
        ).setTitle(place.name)
            .setHeaderAction(Action.BACK)
            .setActionStrip(actionStrip)
            .build()
    }

}
