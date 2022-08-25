package com.yura.interstoryapp.data.remote

import com.yura.interstoryapp.data.remote.response.LoginResponse
import com.yura.interstoryapp.data.remote.response.RegisterAndUploadResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

interface IApi {
    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @FormUrlEncoded
    @POST("register")
    fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<RegisterAndUploadResponse>
}