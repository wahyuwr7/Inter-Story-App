package com.yura.interstoryapp.ui.auth.register

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yura.interstoryapp.data.remote.ApiConfig
import com.yura.interstoryapp.data.remote.response.RegisterAndUploadResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel : ViewModel() {
    fun register(
        name: String,
        email: String,
        password: String,
        context: Context
    ): LiveData<Boolean> {
        val isRegistered = MutableLiveData<Boolean>()
        val service = ApiConfig.getApiService()

        service.register(name, email, password)
            .enqueue(object : Callback<RegisterAndUploadResponse> {
                override fun onResponse(
                    call: Call<RegisterAndUploadResponse>,
                    response: Response<RegisterAndUploadResponse>
                ) {
                    val responseBody = response.body()
                    if (responseBody?.error == false) {
                        isRegistered.postValue(true)
                        Toast.makeText(context, responseBody.message.toString(), Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        isRegistered.postValue(false)
                        Toast.makeText(
                            context,
                            responseBody?.message.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<RegisterAndUploadResponse>, t: Throwable) {
                    isRegistered.postValue(false)
                    Toast.makeText(context, t.message.toString(), Toast.LENGTH_SHORT).show()
                }
            })

        return isRegistered
    }
}