package com.yura.interstoryapp.ui.stories.popup

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.yura.interstoryapp.data.utils.Utils.userAuth
import com.yura.interstoryapp.data.local.prefs.UserPrefs
import kotlinx.coroutines.launch

class PopupLogoutViewModel(private val pref: UserPrefs) : ViewModel() {

    fun deleteUserPrefs() {
        viewModelScope.launch {
            pref.deletePrefs()
            userAuth = ""
        }
    }

    fun getUsername(): LiveData<String> {
        return pref.getUserName().asLiveData()
    }
}