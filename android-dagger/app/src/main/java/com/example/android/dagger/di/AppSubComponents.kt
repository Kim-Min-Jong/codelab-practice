package com.example.android.dagger.di

import com.example.android.dagger.registration.RegistrationComponent
import com.example.android.dagger.user.UserComponent
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent

// @InstallIn 어노테이션은 매개변수를 사용하여 그 요소에 이 모듈을 추가함
// 여기서는 hilt 기본 컴포넌트인 ApplicationComponent에
// AppSubComponent라는 모듈을 알리는 역할
// 이러면 애플리케이션 수준 컴포넌트를 이전할 때 ApplicationComponent에서 결합이 됨
@InstallIn(ApplicationComponent::class)
// SubComponent 선언을 했지만 @Component에 알려지지 않았으니
// Module로 만들어 알려줌
@Module(
    subcomponents = [
        RegistrationComponent::class,
        LoginComponent::class,
        UserComponent::class
    ]
)
class AppSubComponents
