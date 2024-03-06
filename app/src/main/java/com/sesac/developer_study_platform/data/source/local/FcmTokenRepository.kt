package com.sesac.developer_study_platform.data.source.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val FCM_TOKEN_DATASTORE = "fcm_token_datastore"

val Context.fcmTokenDataStore: DataStore<Preferences> by preferencesDataStore(FCM_TOKEN_DATASTORE)

class FcmTokenRepository(private val context: Context) {

    suspend fun setToken(token: String) {
        context.fcmTokenDataStore.edit { preferences ->
            preferences[FCM_TOKEN_KEY] = token
        }
    }

    fun getToken(): Flow<String> {
        return context.fcmTokenDataStore.data
            .map { preferences ->
                preferences[FCM_TOKEN_KEY] ?: ""
            }
    }

    companion object {
        private val FCM_TOKEN_KEY = stringPreferencesKey("fcm_token")
    }
}