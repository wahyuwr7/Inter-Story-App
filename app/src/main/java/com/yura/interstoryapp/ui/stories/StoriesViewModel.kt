package com.yura.interstoryapp.ui.stories

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.yura.interstoryapp.data.local.prefs.UserPrefs
import com.yura.interstoryapp.data.remote.ApiConfig
import com.yura.interstoryapp.data.remote.data.StoriesRepository
import com.yura.interstoryapp.data.remote.response.ListStoryItem
import com.yura.interstoryapp.data.remote.response.StoriesResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoriesViewModel(private val pref: UserPrefs, repository: StoriesRepository) : ViewModel() {

    val stories: LiveData<PagingData<ListStoryItem>> =
        repository.getStories().cachedIn(viewModelScope)

    fun getUsername(): LiveData<String> {
        return pref.getUserName().asLiveData()
    }

}