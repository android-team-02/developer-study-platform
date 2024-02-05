package com.sesac.developer_study_platform.data.source.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferencesRepository(private val context: Context) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(USER_PREFERENCES_NAME)

    suspend fun setAutoLogin() {
        context.dataStore.edit { settings ->
            settings[AUTO_LOGIN] = true
        }
    }

    val autoLogin: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[AUTO_LOGIN] ?: false
        }

    companion object {
        private const val USER_PREFERENCES_NAME = "user_preferences"
        private val AUTO_LOGIN = booleanPreferencesKey("auto_login")
    }
}