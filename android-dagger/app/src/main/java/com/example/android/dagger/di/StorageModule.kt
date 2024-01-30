package com.example.android.dagger.di

import android.content.Context
import com.example.android.dagger.storage.SharedPreferencesStorage
import com.example.android.dagger.storage.Storage
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Qualifier

// dagger는 인터페이스를 직접 인스턴스화 할 수 없기 때문에 제공 방법을 달리 해야한다 (interface Storage)
// dagger에게 Storage를 알려줘야 하는데
// 이 경우 Storage를 상속 받는 SharedPreferenceStorage를 이용해 알려 줄 수 있다.

// @Module
// Component와 비슷하게 Module도 dagger에게 특정 타입의 인스턴스를 알려준다.
// 단, 모듈 내에서는 @Provides와 @Binds 어노테이션에 의해 종속성이 결정된다.

//@Module
//abstract class StorageModule {
//
//    // Storage interface를 dagger에 알리기 위해 @Binds 메소드 사용
//    // @Binds를 붙인 메소드는 추상 함수로 만들어 줘야함
//    // 리턴타입은 반환하고 싶은 인터페이스 타입을 지정
//    // 매개변수로는 그 인터페이스를 구체화하는 클래스 등을 주입
//    // 이렇게 하면 dagger는 Storage interface를 알게됨
//
//    // Storage가 요청될 때, dagger는 이 부분에서 Storage를 찾지만
//    // SharedPreferenceStorage는 아직 dagger에 알려지지 않았음
//    // 따로 어노테이션을 달아, dagger에 알려야함.
//    @Binds
//    abstract fun provideStorage(storage: SharedPreferencesStorage): Storage
//
//}


@Module
class StorageModule {
    // @Inject나 @Binds 외에도 dagger에 클래스 인스턴스를 제공할 수 있는 방법이 있다.
    // 바로 @Provides 이다.
    // @Provides 는 그래프에 어떤 유형이 추가되는지 dagger에게 알려준다
    // 이 함수의 매개변수는 해당 유형의 인스턴스를 제공하기 전 dagger에게 필요한 종속성이다.

    // @Provides는 이 함수 반환타입의 인스턴스를 만드는 방법을 dagger에 알림
    // 매개변수는 반환 타입(Storage)에 종속성을 가짐

    @RegistrationStorage
    @Provides
    fun provideRegistrationStorage(context: Context): Storage =
        // dagger에게 Storage 인스턴스를 제공
        SharedPreferencesStorage("registration", context)

    @LoginStorage
    @Provides
    fun provideLoginStorage(context: Context): Storage =
        // dagger에게 Storage 인스턴스를 제공
        SharedPreferencesStorage("login", context)


    /**
     * @Provides을 사용하여 dagger에게 알리는 방법
     * 1. 인터페이스 구현 (단, @Binds가 더 적은 코드로 작성할 수 있으로 추천됨)
     * 2. 프로젝트가 갖고 있지 않은 클래스들.. (ex) retrofit (외부 라이브러리...)
     */
}

// 이 프로젝트는 단순하기 때문에 dagger qualifiers를 사용할 필요가 없었다.
// qualifiers를 알아보자
// qualifiers는 dagger 그래프에 동일한 유형의 다른 구현을 추가할 떄 아주 유용하다.
// 예를 들면, 서로 다른 Storage를 제공하려면 qualifiers를 통해 구별 할 수 있다.

// A qualifier is a custom annotation that will be used to identify a dependency.
// 정의 - 커스텀 어노테이션으로써 종속성을 개별 식별할 수 있다.

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class RegistrationStorage

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class LoginStorage
