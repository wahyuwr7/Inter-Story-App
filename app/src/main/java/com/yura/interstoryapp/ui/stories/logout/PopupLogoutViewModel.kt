package com.yura.interstoryapp.ui.stories.logout

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.*
import com.yura.interstoryapp.data.local.prefs.UserPrefs
import com.yura.interstoryapp.data.remote.ApiConfig
import com.yura.interstoryapp.data.remote.response.ListStoryItem
import com.yura.interstoryapp.data.remote.response.StoriesResponse
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PopupLogoutViewModel(private val pref: UserPrefs) : ViewModel() {

    fun deleteUserPrefs() {
        viewModelScope.launch {
            pref.deletePrefs()
        }
    }

    fun getUsername() : LiveData<String>{
        return pref.getUserName().asLiveData()
    }
}