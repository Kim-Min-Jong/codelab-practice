package com.example.android.dagger.di

import android.content.Context
import com.example.android.dagger.main.MainActivity
import com.example.android.dagger.registration.RegistrationActivity
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

// 다양한 이유로 같은 인스턴스를 제공하는 것을 원할 때도 있다. (같은 인스턴스를 공유하는 다른 타입의 객체를 만들고 싶을떄.. 매번 인스턴스를 만들때마다 비용이 클 때 등..)
// 이러한 특별한 인스턴스를 같는 것이 있다. (Scopes)
// 이건 Component 생명주기의 스코프라 불리곤 한다.
// 이는  구성 요소의 수명 주기에 따라 유형 범위를 지정 "이라고도 한다.
// 타입을 구성 요소로 범위 지정한다는 것은 타입을 제공해야 할 때마다 해당 타입의 동일한 인스턴스가 사용된다는 의미이다.
// 이것은 한 번 만들어지면 생명주기 동안 계속 유지되는 특성이 있다.
// dagger에서는 @Singleton 어노테이션을 통해 위와 같은 기능을 수행할 수 있다.
@Singleton

// dagger 프로젝트가 프로젝트의 종속 그래프를 생성하고, 관리하며 종석성을 얻을 수 있기 원한다.
// 이를 위해서 인터페이스를 생성하고 @Component 어노테이션을 붙일수 있다.
// @Component는 Dagger가 갖고있는 매개변수를 충족하는데 필요한 모든 종속성을 포함시킨 코드를 생성한다.
// 해당 인터페이스에서 dagger에 주입을 요청할 수 있다.

// 컴파일 타임에 종속성 그래프를 생성하는데 필요한 정보를 제공
@Component(
    // module을 그래프 생성시 dagger에게 알려줌
    modules = [StorageModule::class]
)
interface AppComponent {

    // SharedPreferenceStorage에서 생성자로 context를 받음
    // 하지만 이것을 dagger는 받지 않았으므로 알지 못함
    // 명시 해주어야함
    // context는 안드로이드 시스템에서 제공되므로 그래프 외부에 구성됨
    // 그래프의 인스턴스를 생성(컴파일 단계)에서 사용가능하므로 이때 전달해줘야함
    // 이때, Factory와 @BindsInstance를 사용할 수 있음

    // Factory 패턴
    @Component.Factory
    interface Factory {
        // @BindsInstance는 그래프 외부의 객체를 가져와 사용할 수 있음
        // @BindsInstance에 의해 context를 가진 Appcomponent가 반환됨
        fun create(@BindsInstance context: Context): AppComponent
    }

    // dagger에 RegistratinActivity를 주입하도록 요청하고
    // @Inject 어노테이션이 달린 종속성을 제공해야한다고 알림

    // 하지만 RegistrationActivity 에서는 RegistrationViewModel을
    // @Inject로 주입을 받고 있는데 (UserManager, Storage) ,
    // 이 인터페이스 내에선 Dagger가 그 종속성을 알지 못함
    // 컴파일 타임에 에러가 발생함
    fun inject(activity: RegistrationActivity)

    // MainActivity도 똑같이 주입
    fun inject(activity: MainActivity)
}
