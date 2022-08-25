package com.yura.interstoryapp.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.yura.interstoryapp.data.local.prefs.UserPrefs

class SplashViewModel(private val pref: UserPrefs) : ViewModel() {
    fun getUserLoginState(): LiveData<Boolean> {
        return pref.getUserLoginState().asLiveData()
    }
}