package com.yura.interstoryapp.ui.auth.login

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yura.interstoryapp.data.local.prefs.UserPrefs
import com.yura.interstoryapp.data.remote.ApiConfig
import com.yura.interstoryapp.data.remote.response.LoginResponse
import com.yura.interstoryapp.data.remote.response.LoginResult
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(
    private val pref: UserPrefs
) : ViewModel() {
    fun login(email: String, password: String, context: Context): LiveData<Boolean>{
        val isLogin = MutableLiveData<Boolean>()
        val service = ApiConfig.getApiService()

        service.login(email, password).enqueue(object : Callback<LoginResponse>{
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                val responseBody = response.body()
                if (responseBody?.error == false){
                    LoginResponse(
                        LoginResult(
                            name = responseBody.loginResult?.name,
                            userId = responseBody.loginResult?.userId,
                            token = responseBody.loginResult?.token
                        )
                    )
                    responseBody.loginResult?.let { saveUserData(it, email) }
                    isLogin.postValue(true)
                    Toast.makeText(context, responseBody.message.toString(), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, responseBody?.message.toString(), Toast.LENGTH_SHORT).show()
                    isLogin.postValue(false)
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(context, t.message.toString(), Toast.LENGTH_SHORT).show()
                isLogin.postValue(false)
            }
        })
        return isLogin
    }

    fun saveUserData(userData: LoginResult, email: String) {
        viewModelScope.launch {
            pref.saveUserEmail(email)
            userData.name?.let { pref.saveUserName(it) }
            userData.token?.let { pref.saveUserToken(it) }
            pref.saveUserLoginState(true)
        }
    }
}