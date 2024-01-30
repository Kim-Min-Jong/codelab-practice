package com.example.android.dagger.di

import com.example.android.dagger.login.LoginActivity
import dagger.Subcomponent

// Registration flow 처럼 Login flow도 dagger에 맞춤

// 다른 생명주기를 같는 객체들은 SubComponent를 만드는 것이 앱 구조 캡슐화에 좋은 방법이다.
// 이렇게 하위 요소로 flow를 나누게 되면 앱은 더 퍼포먼스를 갖게되고 확장 가능성이 생긴다.
// 그리고 기존의 모놀리틱 방식과 달리 모듈화와 코드 읽기가 더 쉬워진다.

// 액티비티에만 유지되는 스코프
@ActivityScope
// dagger SubComponent
@Subcomponent
interface LoginComponent {

    // factory로 Component 생성 하기 위한 인터페이스
    @Subcomponent.Factory
    interface Factory {
        fun create(): LoginComponent
    }

    // LoginComponent에 주입될 액티비티를 받는 메소드
    fun inject(activity: LoginActivity)
}
