package com.example.android.dagger

import com.example.android.dagger.storage.FakeStorage
import com.example.android.dagger.storage.Storage
import dagger.Binds
import dagger.Module

@Module
abstract class TestStorageModule {
    // 테스트를 위해 FakeStorage를 이용
    @Binds
    abstract fun provideStorage(storage: FakeStorage): Storage
}
