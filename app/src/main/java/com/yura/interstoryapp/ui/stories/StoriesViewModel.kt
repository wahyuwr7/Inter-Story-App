package com.yura.interstoryapp.ui.stories

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.*
import com.yura.interstoryapp.data.local.prefs.UserPrefs
import com.yura.interstoryapp.data.remote.ApiConfig
import com.yura.interstoryapp.data.remote.response.ListStoryItem
import com.yura.interstoryapp.data.remote.response.StoriesResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoriesViewModel(private val pref: UserPrefs) : ViewModel() {

    fun getStories(token: String, context: Context): LiveData<ArrayList<ListStoryItem?>?> {
        val auth = "Bearer $token"
        val result = MutableLiveData<ArrayList<ListStoryItem?>?>()
        val service = ApiConfig.getApiService().getStories(auth)

        service.enqueue(object : Callback<StoriesResponse> {
            override fun onResponse(
                call: Call<StoriesResponse>,
                response: Response<StoriesResponse>
            ) {
                val responseBody = response.body()
                if (responseBody?.error == false) {
                    result.value = responseBody.listStory
                } else {
                    Toast.makeText(context, "${responseBody?.message}", Toast.LENGTH_SHORT).show()
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

    fun getUsername() : LiveData<String>{
        return pref.getUserName().asLiveData()
    }

}