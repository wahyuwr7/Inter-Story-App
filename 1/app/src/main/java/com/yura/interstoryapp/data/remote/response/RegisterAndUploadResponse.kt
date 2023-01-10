package com.yura.interstoryapp.data.remote.response

import com.google.gson.annotations.SerializedName

data class RegisterAndUploadResponse(

    @field:SerializedName("error")
    val error: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null
)
