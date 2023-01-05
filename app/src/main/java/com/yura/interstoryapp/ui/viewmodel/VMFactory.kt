package com.yura.interstoryapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yura.interstoryapp.data.local.prefs.UserPrefs
import com.yura.interstoryapp.ui.auth.login.LoginViewModel
import com.yura.interstoryapp.ui.auth.register.RegisterViewModel
import com.yura.interstoryapp.ui.splash.EnterAppViewModel
import com.yura.interstoryapp.ui.stories.StoriesViewModel
import com.yura.interstoryapp.ui.stories.add.AddStoryViewModel
import com.yura.interstoryapp.ui.stories.logout.PopupLogoutViewModel

class VMFactory(private val pref: UserPrefs) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EnterAppViewModel::class.java)) {
            return EnterAppViewModel(pref) as T
        }
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(pref) as T
        }
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            return RegisterViewModel() as T
        }
        if (modelClass.isAssignableFrom(StoriesViewModel::class.java)) {
            return StoriesViewModel(pref) as T
        }
        if (modelClass.isAssignableFrom(AddStoryViewModel::class.java)) {
            return AddStoryViewModel(pref) as T
        }
        if (modelClass.isAssignableFrom(PopupLogoutViewModel::class.java)) {
            return PopupLogoutViewModel(pref) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}