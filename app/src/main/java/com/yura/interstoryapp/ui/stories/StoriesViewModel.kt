package com.yura.interstoryapp.ui.stories

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.yura.interstoryapp.data.local.prefs.UserPrefs
import com.yura.interstoryapp.data.remote.repository.StoriesRepository
import com.yura.interstoryapp.data.remote.response.ListStoryItem

class StoriesViewModel(private val pref: UserPrefs, repository: StoriesRepository) : ViewModel() {

    val stories: LiveData<PagingData<ListStoryItem>> =
        repository.getStories().cachedIn(viewModelScope)

    fun getUsername(): LiveData<String> {
        return pref.getUserName().asLiveData()
    }

}