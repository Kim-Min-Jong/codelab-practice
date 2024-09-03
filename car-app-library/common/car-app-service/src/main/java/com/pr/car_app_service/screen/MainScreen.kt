package com.pr.car_app_service.screen

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.Pane
import androidx.car.app.model.PaneTemplate
import androidx.car.app.model.Row
import androidx.car.app.model.Template

class MainScreen(carContext: CarContext): Screen(carContext) {
    // 보여줄 스크린 모양을 코드를 통해 생성
    override fun onGetTemplate(): Template {
        // 행 생성
        val row = Row.Builder()
            .setTitle("Hello World!")
            .build()
        // 행을 담은 공간 생성
        val pane = Pane.Builder()
            .addRow(row)
            .build()

        // 헤더를 포함한 전체 템플릿 생성
        return PaneTemplate.Builder(pane)
            .setHeaderAction(Action.APP_ICON)
            .build()
    }

}
