package com.yura.interstoryapp.data.remote

import com.yura.interstoryapp.data.remote.response.LoginResponse
import com.yura.interstoryapp.data.remote.response.RegisterAndUploadResponse
import com.yura.interstoryapp.data.remote.response.StoriesResponse
import retrofit2.Call
import retrofit2.http.*

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

    @GET("stories")
    fun getStories(
        @Header("Authorization") auth : String
    ): Call<StoriesResponse>
}