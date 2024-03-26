package com.example.dps
import com.google.gson.annotations.SerializedName

data class FindPwResponse_login(
    @SerializedName("code") val code: Int,
    @SerializedName("message") val message: String
)
