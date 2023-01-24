package com.yura.interstoryapp.data.remote.data

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.yura.interstoryapp.data.local.db.StoryDb
import com.yura.interstoryapp.data.remote.IApi
import com.yura.interstoryapp.data.remote.response.ListStoryItem

class StoriesRepository(private val db: StoryDb, private val apiService: IApi, private val context: Context) {
    fun getStories(): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                StoriesPagingSource(apiService, context)
            }
        ).liveData
    }
}