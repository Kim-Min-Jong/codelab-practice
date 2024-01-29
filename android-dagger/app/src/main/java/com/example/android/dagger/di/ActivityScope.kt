package com.example.android.dagger.di

import javax.inject.Scope

// SubComponent에 대한 스코프도 필요한데, 메인 컴포넌트인 AppComponent가 Singleton Scope를 유지하고 있으므로 다른 인스턴스를 만들어줄 필요가 있다.
// 새로 만드는데, RegistrationScope처럼 특정 작업에 해당하는 스코프를 만드는 것은 바람직하지 않다.
// 어노테이션은 component에서 재사용이 되므로 수명 주기에 따라 이름을 지정해야한다.
// 그래서 activity 단위로 유지될 Activity Scope를 생성한다.

/**
 * Scope 생성 규칙
 * 1. 타입이 @Scope로 어노테이션이 지정되면, 같은 범위로 지정된 component에서만 사용할 수 있다.
 * 2. 컴포넌트가 @Scope 어노테이션으로 지정되면, 해당 어노테이션이 있는 타입이나 없는 타입만 제공할 수 있다.
 * 3. SubComponent는 상위 요소중 하나에서 사용되는 스코프를 사용할 수 없다.
 */

@Scope
@MustBeDocumented
@Retention(value = AnnotationRetention.RUNTIME)
annotation class ActivityScope
