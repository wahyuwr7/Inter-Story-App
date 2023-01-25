package com.yura.interstoryapp.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yura.interstoryapp.data.local.prefs.UserPrefs
import com.yura.interstoryapp.di.Injection
import com.yura.interstoryapp.ui.auth.login.LoginViewModel
import com.yura.interstoryapp.ui.auth.register.RegisterViewModel
import com.yura.interstoryapp.ui.splash.EnterAppViewModel
import com.yura.interstoryapp.ui.stories.StoriesViewModel
import com.yura.interstoryapp.ui.stories.add.AddStoryViewModel
import com.yura.interstoryapp.ui.stories.maps.MapsViewModel
import com.yura.interstoryapp.ui.stories.popup.PopupLogoutViewModel

class VMFactory(private val prefs: UserPrefs, private val context: Context) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EnterAppViewModel::class.java)) {
            return EnterAppViewModel(prefs) as T
        }
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(prefs) as T
        }
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            return RegisterViewModel() as T
        }
        if (modelClass.isAssignableFrom(StoriesViewModel::class.java)) {
            return StoriesViewModel(prefs, Injection.provideRepository()) as T
        }
        if (modelClass.isAssignableFrom(AddStoryViewModel::class.java)) {
            return AddStoryViewModel(prefs) as T
        }
        if (modelClass.isAssignableFrom(PopupLogoutViewModel::class.java)) {
            return PopupLogoutViewModel(prefs) as T
        }
        if (modelClass.isAssignableFrom(MapsViewModel::class.java)) {
            return MapsViewModel(prefs) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}