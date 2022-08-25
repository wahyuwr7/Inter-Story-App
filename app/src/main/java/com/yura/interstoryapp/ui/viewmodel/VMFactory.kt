package com.yura.interstoryapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yura.interstoryapp.data.local.prefs.UserPrefs
import com.yura.interstoryapp.ui.auth.login.LoginViewModel
import com.yura.interstoryapp.ui.auth.register.RegisterViewModel
import com.yura.interstoryapp.ui.splash.SplashViewModel

class VMFactory(private val pref: UserPrefs) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SplashViewModel::class.java)) {
            return SplashViewModel(pref) as T
        }
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(pref) as T
        }
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            return RegisterViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}