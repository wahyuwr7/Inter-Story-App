package com.yura.interstoryapp.di

import android.content.Context
import com.yura.interstoryapp.data.remote.ApiConfig
import com.yura.interstoryapp.data.remote.repository.StoriesRepository

object Injection {
    fun provideRepository(): StoriesRepository {
        val apiService = ApiConfig.getApiService()
        return StoriesRepository(apiService)
    }
}