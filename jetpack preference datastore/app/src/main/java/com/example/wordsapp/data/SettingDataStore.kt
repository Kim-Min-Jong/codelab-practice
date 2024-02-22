package com.example.wordsapp.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

class SettingDataStore(
    context: Context
) {
}
// 인스턴스화할 Preferences DataStore의 이름
private const val LAYOUT_PREFERENCE_NAME = "layout_preference"

// delegate 패턴을 사용하여 dataStore 인스턴스 생성
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = LAYOUT_PREFERENCE_NAME
)
