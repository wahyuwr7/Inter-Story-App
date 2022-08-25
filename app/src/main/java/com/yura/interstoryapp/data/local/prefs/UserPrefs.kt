package com.yura.interstoryapp.data.local.prefs

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPrefs private constructor(private val dataStore: DataStore<Preferences>) {

    private val isLoggedIn = booleanPreferencesKey("isLoggedIn")
    private val name = stringPreferencesKey("name")
    private val email = stringPreferencesKey("email")
    private val token = stringPreferencesKey("token")

    fun getUserLoginState(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[isLoggedIn] ?: false
        }
    }

    suspend fun saveUserLoginState(isUserLoggedIn: Boolean) {
        dataStore.edit { preferences ->
            preferences[isLoggedIn] = isUserLoggedIn
        }
    }

    fun getUserName(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[name] ?: ""
        }
    }

    suspend fun saveUserName(userName: String) {
        dataStore.edit { preferences ->
            preferences[name] = userName
        }
    }

    fun getUserToken(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[token] ?: ""
        }
    }

    suspend fun saveUserToken(userToken: String) {
        dataStore.edit { preferences ->
            preferences[token] = userToken
        }
    }

    fun getUserEmail(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[email] ?: ""
        }
    }

    suspend fun saveUserEmail(userEmail: String) {
        dataStore.edit { preferences ->
            preferences[email] = userEmail
        }
    }

    suspend fun deletePrefs(){
        dataStore.edit { preferences ->
            preferences[email] = ""
            preferences[token] = ""
            preferences[name] = ""
            preferences[isLoggedIn] = false
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPrefs? = null

        fun getInstance(dataStore: DataStore<Preferences>): UserPrefs {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPrefs(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }

}