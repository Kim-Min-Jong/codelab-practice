package com.example.android.dagger.di

import com.example.android.dagger.registration.RegistrationActivity
import dagger.Component

// dagger 프로젝트가 프로젝트의 종속 그래프를 생성하고, 관리하며 종석성을 얻을 수 있기 원한다.
// 이를 위해서 인터페이스를 생성하고 @Component 어노테이션을 붙일수 있다.
// @Component는 Dagger가 갖고있는 매개변수를 충족하는데 필요한 모든 종속성을 포함시킨 코드를 생성한다.
// 해당 인터페이스에서 dagger에 주입을 요청할 수 있다.

// 컴파일 타임에 종속성 그래프를 생성하는데 필요한 정보를 제공
@Component
interface AppComponent {

    // dagger에 RegistratinActivity를 주입하도록 요청하고
    // @Inject 어노테이션이 달린 종속성을 제공해야한다고 알림

    // 하지만 RegistrationActivity 에서는 RegistrationViewModel을
    // @Inject로 주입을 받고 있는데 (UserManager, Storage) ,
    // 이 인터페이스 내에선 Dagger가 그 종속성을 알지 못함
    // 컴파일 타임에 에러가 발생함
    fun inject(activity: RegistrationActivity)
}
