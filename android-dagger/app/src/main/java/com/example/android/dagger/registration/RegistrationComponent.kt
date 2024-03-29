package com.example.android.dagger.registration

import com.example.android.dagger.di.ActivityScope
import com.example.android.dagger.registration.enterdetails.EnterDetailsFragment
import com.example.android.dagger.registration.termsandconditions.TermsAndConditionsFragment
import dagger.Subcomponent

// 뷰모델 스코프 지정
@ActivityScope

// dagger 사용을 위해 fragment에도 inject를 적용하였다.
// 여기서 RegistrationViewModel이 여러 군데에서 사용되게 되는데
// 각 액티비티(프래그먼트) 마다 뷰모델 인스턴스가 공유되지 않는 문제가 있다.
// 여기서 RegistrationViewModel을 @Singleton으로 만드는 방법을 생각할 수 있다.
// 하지만, 이 방법은 잠정적인 문제들을 지니고있다.
// 1. flow가 끝나고 메모리에서 해제되길 바라는데 Singleton으로 계속 메모리에 남아있게 된다.
// 2. 다른 register flow 마다 같은 뷰모델이 아닌 다른 뷰모델 인스턴스를 이용하고 싶다. (회원 등록, 삭제 등에서 필요한 데이터가 다르기 때문)

// 결국 ViewModel을 재사용하길 원하지만 @Singleton을 사용하지 않으면서, 다른 인스턴스를 사용하길 원한다.
// 이를 위해, dagger에서는 flow에 따른 새로운 component를 만들고 ViewModel의 범위를 해당 component로 새로 지정할 수 있다.
// SubComponent를 이용하면 된다!

// @SubComponent
// SubComponent는 상위 Component 요소의 그래프를 상속하고 확장하는 component이다.
// 따라서, 상위 요소에 있는 모든 인스턴스들은 하위 요소에도 제공된다.
// 이런 방식으로 하위 요소의 인스턴스는 상위 요소에서 제공하는 인스턴스에 종속성을 갖게된다.
@Subcomponent
interface RegistrationComponent {

    // Component(instance)를 새로 만들기 위한 factory 인터페이스)
//    @Subcomponent.Factory
//    interface Factory {
//        fun create(): RegistrationComponent
//    }

    // 상위 요소를 갖는 하위요소로써 새로 만들어 낸다.
    fun inject(activity: RegistrationActivity)
    fun inject(fragment: EnterDetailsFragment)
    fun inject(fragment: TermsAndConditionsFragment)

}

/**
 * RegistrationComponent가 activity 및 fragment에 연결 전에는 application이 메모리에 올라와 있고
 * 동일한 그래프 인스턴스를 사용하려고 하기 때문에 application의 수명주기가 연결된다.
 *
 *
 * 하위 컴포넌트가 필요한 이유중 하나는 RegistratinViewModel이 액티비티와 프래그먼트가 새로운 flow마다
 * 새로운 인스턴스를 원하는데, 상위가 Singleton으로 있으면 그러지 못한다.
 *
 * 그래서 Registration Activity의 모든 새로운 flow에 인스턴스를 만들기 위해 새로운 하위 컴포넌트를 만든다.
 *
 * RegistrationComponent 도 결국 Registration Activity에 수명주기가 연결되어있기 떄문에, 이전에 연결했던
 * application 방식과 동일한 방식으로 참조를 유지하여, fragment에도 접근할 수 있다.
 *
 */
