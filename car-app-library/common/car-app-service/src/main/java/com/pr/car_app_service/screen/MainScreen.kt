package com.pr.car_app_service.screen

import android.text.Spannable
import android.text.SpannableString
import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.CarLocation
import androidx.car.app.model.Distance
import androidx.car.app.model.DistanceSpan
import androidx.car.app.model.ItemList
import androidx.car.app.model.Metadata
import androidx.car.app.model.Pane
import androidx.car.app.model.PaneTemplate
import androidx.car.app.model.Place
import androidx.car.app.model.PlaceListMapTemplate
import androidx.car.app.model.PlaceMarker
import androidx.car.app.model.Row
import androidx.car.app.model.Template
import com.example.places.data.PlacesRepository

class MainScreen(carContext: CarContext) : Screen(carContext) {
    // 보여줄 스크린 모양을 코드를 통해 생성
    override fun onGetTemplate(): Template {
        // 데이터
        val placeRepository = PlacesRepository()
        // 아이템 리스트를 보여줄 객체
        val itemListBuilder = ItemList.Builder()
            .setNoItemsMessage("No Places to show")

        placeRepository.getPlaces()
            .forEach {
                itemListBuilder.addItem(
                    Row.Builder()
                        .setTitle(it.name)
                        .addText(
                            // 리스트의 각아이템에는 일정 SPAN이 반드시 필요함
                            SpannableString(" ").apply {
                                setSpan(
                                    DistanceSpan.create(
                                        Distance.create(
                                            Math.random() * 100,
                                            Distance.UNIT_KILOMETERS
                                        )
                                    ),
                                    0,
                                    1,
                                    Spannable.SPAN_INCLUSIVE_INCLUSIVE
                                )
                            }
                        ).setOnClickListener { screenManager.push(DetailScreen(carContext, it.id)) }
                        // 메타데이터 설정은 선택적이지만 제공되는 지도에 자동적으로 아이템의 위치를 보여줌으로 필요
                        .setMetadata(
                            Metadata.Builder()
                                .setPlace(
                                    Place.Builder(
                                        CarLocation.create(it.latitude, it.longitude)
                                        // 마커 찍기
                                    ).setMarker(PlaceMarker.Builder().build())
                                        .build()
                                ).build()
                        ).build())
            }

        return PlaceListMapTemplate.Builder()
            .setTitle("Places")
            .setItemList(itemListBuilder.build())
            .build()

    }
}
