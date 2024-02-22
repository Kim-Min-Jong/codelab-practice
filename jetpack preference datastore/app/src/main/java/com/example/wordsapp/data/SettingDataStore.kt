package com.example.wordsapp.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class SettingDataStore(
    context: Context
) {
    // 각 키를 정의 할 때는 타입에 대응하는 키 유형 함수를 사용

    // Linear 인지 Grid인지 구분하는 구분자 dataStore
    private val IS_LINEAR_LAYOUT_MANAGER = booleanPreferencesKey("is_linear_layout_manager")

    // 읽는 변수 (로직)
    // Preferences DataStore는 환경설정이 변경될 때마다 내보내는 Flow<Preferences>에 저장된 데이터를 노출
    // 전체 Preferences 객체를 노출하지 않고 Boolean 값만 노출하는 것이 좋음 (단일 값만)
    val preferenceFlow: Flow<Boolean> = context.dataStore.data
        // 예외 처리
        .catch {
            if (it is IOException) {
                it.printStackTrace()
                // 실패하면 빈 값을 flow로 보냄
                emit(emptyPreferences())
            } else {
                throw it
            }
        }.map { preferences ->
            preferences[IS_LINEAR_LAYOUT_MANAGER] ?: true
        }

    // 값을 저장하는 함수
    // Preference DataStore는 트랜잭션 방식으로 진행하는 edit() suspend 함수를 제공함
    // 해당 블록의 코드는 단일 트랜잭션으로 취급됨
    // 내부 트랜잭션은 Dispatcher.IO에서 수행되므로 suspend해야함
    suspend fun saveLayoutToPreferenceStore(
        isLinearLayoutManager: Boolean,
        context: Context
    ) {
        // 트랜잭션으로 동작
        context.dataStore.edit { preferences ->
            preferences[IS_LINEAR_LAYOUT_MANAGER] = isLinearLayoutManager
        }
    }

}

// 인스턴스화할 Preferences DataStore의 이름
private const val LAYOUT_PREFERENCE_NAME = "layout_preference"

// delegate 패턴을 사용하여 dataStore 인스턴스 생성
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = LAYOUT_PREFERENCE_NAME
)
