package com.yura.interstoryapp.ui.stories.add

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.yura.interstoryapp.R
import com.yura.interstoryapp.data.local.prefs.UserPrefs
import com.yura.interstoryapp.data.remote.ApiConfig.getApiService
import com.yura.interstoryapp.data.remote.response.RegisterAndUploadResponse
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddStoryViewModel(private val pref: UserPrefs) : ViewModel() {

    fun uploadStory(
        token: String,
        desc: String,
        imageMultipart: MultipartBody.Part,
        context: Context
    ): LiveData<Boolean> {
        val state = MutableLiveData<Boolean>()
        val description = desc.toRequestBody("text/plain".toMediaType())
        val userToken = "Bearer $token"
        val service = getApiService().uploadImage(userToken, imageMultipart, description)

        service.enqueue(object : Callback<RegisterAndUploadResponse> {
            override fun onResponse(
                call: Call<RegisterAndUploadResponse>,
                response: Response<RegisterAndUploadResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && responseBody.error == false) {
                        Toast.makeText(context, responseBody.message, Toast.LENGTH_SHORT)
                            .show()
                    }
                    state.value = true
                } else {
                    Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show()
                    state.value = false
                }
            }

            override fun onFailure(call: Call<RegisterAndUploadResponse>, t: Throwable) {
                Toast.makeText(context, context.getString(R.string.net_failed), Toast.LENGTH_SHORT)
                    .show()
                state.value = false
            }
        })

        return state
    }

    fun getUserToken(): LiveData<String> {
        return pref.getUserToken().asLiveData()
    }
}