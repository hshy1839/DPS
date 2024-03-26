package com.example.dps
import com.google.gson.annotations.SerializedName

data class FindPwData_login(
    @SerializedName("password") val password: String,
    @SerializedName("username") val username: String
)
