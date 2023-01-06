package com.yura.interstoryapp.data.remote

import com.yura.interstoryapp.data.remote.response.LoginResponse
import com.yura.interstoryapp.data.remote.response.RegisterAndUploadResponse
import com.yura.interstoryapp.data.remote.response.StoriesResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
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
        @Header("Authorization") auth: String
    ): Call<StoriesResponse>

    @Multipart
    @POST("stories")
    fun uploadImage(
        @Header("Authorization") auth: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
    ): Call<RegisterAndUploadResponse>
}