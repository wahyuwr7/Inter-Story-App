package com.yura.interstoryapp.ui.stories.logout

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.yura.interstoryapp.data.local.prefs.UserPrefs
import kotlinx.coroutines.launch

class PopupLogoutViewModel(private val pref: UserPrefs) : ViewModel() {

    fun deleteUserPrefs() {
        viewModelScope.launch {
            pref.deletePrefs()
        }
    }

    fun getUsername(): LiveData<String> {
        return pref.getUserName().asLiveData()
    }
}