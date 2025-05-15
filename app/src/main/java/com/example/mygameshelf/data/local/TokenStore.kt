package com.example.mygameshelf.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenStore @Inject constructor(private val dataStore: DataStore<Preferences>) {

    companion object {
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val EXPIRES_AT = longPreferencesKey("expires_at") // epoch millis
    }

    suspend fun saveToken(token: String, expiresIn: Int) {
        val expiryMillis = System.currentTimeMillis() + (expiresIn * 1000L)
        dataStore.edit {
            it[ACCESS_TOKEN] = token
            it[EXPIRES_AT] = expiryMillis
        }
    }

    suspend fun getToken(): String? = dataStore.data.first()[ACCESS_TOKEN]

    suspend fun isTokenExpired(): Boolean {
        val now = System.currentTimeMillis()
        val expiry = dataStore.data.first()[EXPIRES_AT] ?: 0L
        return now >= expiry
    }
}