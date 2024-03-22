package com.example.dps.loginActivity

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface RetrofitInterface {
    @POST("/api/android/login")
    fun executeLogin(@Body map: HashMap<String, String>): Call<LoginResult>

    @POST("/api/android/signup")
    fun executeSignup(@Body map: HashMap<String, String>): Call<Void>
}
