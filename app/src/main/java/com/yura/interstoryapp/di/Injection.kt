package com.yura.interstoryapp.di

import android.content.Context
import com.yura.interstoryapp.data.local.db.StoryDb
import com.yura.interstoryapp.data.remote.ApiConfig
import com.yura.interstoryapp.data.remote.data.StoriesRepository

object Injection {
    fun provideRepository(context: Context): StoriesRepository {
        val database = StoryDb.getDatabase(context)
        val apiService = ApiConfig.getApiService()
        return StoriesRepository(database, apiService, context)
    }
}