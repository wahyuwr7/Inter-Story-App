package com.yura.interstoryapp.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

object Utils {
    const val baseUrl = "https://story-api.dicoding.dev/v1/"
    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
}