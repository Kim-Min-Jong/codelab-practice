package com.example.android.dagger.di

import com.example.android.dagger.registration.RegistrationComponent
import com.example.android.dagger.user.UserComponent
import dagger.Module

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
