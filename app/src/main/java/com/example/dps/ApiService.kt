package com.example.dps

import com.example.dps.loginActivity.ResponseDTO
import retrofit2.Call
import retrofit2.http.*

data class ResponseDC(var result:String? = null)

interface ApiService {
    @GET("/api/android/login")
    fun getRequest(@Query("username") username: String, @Query("password") password: String): Call<ResponseDTO>

    @FormUrlEncoded
    @POST("/api/android/login")
    fun postRequest(@Field("id")id: String,
                    @Field("password")password: String):Call<ResponseDC>
}