package com.example.android.dagger

import com.example.android.dagger.storage.FakeStorage
import com.example.android.dagger.storage.Storage
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent

// 테스트를 위해 hilt에 추가
@InstallIn(ApplicationComponent::class)
@Module
abstract class TestStorageModule {
    // 테스트를 위해 FakeStorage를 이용
    @Binds
    abstract fun provideStorage(storage: FakeStorage): Storage
}
