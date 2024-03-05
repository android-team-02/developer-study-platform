package com.sesac.developer_study_platform.data.source.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val USER_PREFERENCES_NAME = "user_preferences"

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(USER_PREFERENCES_NAME)

class UserPreferencesRepository @Inject constructor(@ApplicationContext private val context: Context) {

    suspend fun setAutoLogin(autoLogin: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[AUTO_LOGIN] = autoLogin
        }
    }

    val autoLogin: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[AUTO_LOGIN] ?: false
        }

    companion object {
        private val AUTO_LOGIN = booleanPreferencesKey("auto_login")
    }
}