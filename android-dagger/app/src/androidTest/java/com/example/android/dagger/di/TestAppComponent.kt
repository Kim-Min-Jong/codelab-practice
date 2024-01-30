package com.example.android.dagger.di

import com.example.android.dagger.TestStorageModule
import dagger.Component
import javax.inject.Singleton


// 테스트용 AppComponent
@Singleton
@Component(
    modules = [
        TestStorageModule::class,
        AppSubComponents::class
    ]
)
interface TestAppComponent : AppComponent
