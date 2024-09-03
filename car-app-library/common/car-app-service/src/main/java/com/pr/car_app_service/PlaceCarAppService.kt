package com.pr.car_app_service

import android.content.Intent
import androidx.car.app.CarAppService
import androidx.car.app.Screen
import androidx.car.app.Session
import androidx.car.app.validation.HostValidator
import com.pr.car_app_service.screen.MainScreen

// CarAppService는 Service의 하위 클래스 (서비스처럼 동작)
class PlaceCarAppService: CarAppService() {

    // 지정한 사용자만 서비스를 사용할 수 있도록 함
    override fun createHostValidator(): HostValidator {
        // 모든 사용자 가능 (실제 프로덕트에선 쓰이지 않음 - 보안문제?)
        return HostValidator.ALLOW_ALL_HOSTS_VALIDATOR
    }

    // 세션 생성
    // 세션은 차량 화면에 보여줄 스크린을 갖고있는 객체
    // 라이프사이클을 갖고있음
    override fun onCreateSession(): Session {
        return PlacesSession()
    }
}

class PlacesSession(): Session() {
    override fun onCreateScreen(intent: Intent): Screen {
        // 화면을 보여줄 스크린 반환
        return MainScreen(carContext)
    }
}
