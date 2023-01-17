package com.yura.interstoryapp.ui.stories.maps

import android.content.Context
import android.graphics.Bitmap
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.yura.interstoryapp.data.Utils
import com.yura.interstoryapp.data.local.prefs.UserPrefs
import com.yura.interstoryapp.data.remote.ApiConfig
import com.yura.interstoryapp.data.remote.response.ListStoryItem
import com.yura.interstoryapp.data.remote.response.StoriesResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapsViewModel(private val pref: UserPrefs) : ViewModel() {

    fun getMapStories(token: String, context: Context): LiveData<ArrayList<ListStoryItem?>?> {
        val auth = "Bearer $token"
        val result = MutableLiveData<ArrayList<ListStoryItem?>?>()
        val service = ApiConfig.getApiService().getStoriesWithLocation(auth, 1)

        service.enqueue(object : Callback<StoriesResponse> {
            override fun onResponse(
                call: Call<StoriesResponse>,
                response: Response<StoriesResponse>
            ) {
                val responseBody = response.body()
                if (responseBody?.error == false) {
                    result.value = responseBody.listStory
                }
            }

            override fun onFailure(call: Call<StoriesResponse>, t: Throwable) {
                Toast.makeText(context, "${t.message}", Toast.LENGTH_SHORT).show()
            }
        })

        return result
    }

    fun getUserToken(): LiveData<String> {
        return pref.getUserToken().asLiveData()
    }
}
