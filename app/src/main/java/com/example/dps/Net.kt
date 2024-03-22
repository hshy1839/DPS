package com.example.dps

import android.util.Log
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory


class Net private constructor() {
    var gson = GsonBuilder().setLenient().create()
    var client = OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor()).build()
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://15.164.57.70") //<= 서버 주소값 입력
        .client(client) //<= OkHttpClient 연동
        .addConverterFactory(ScalarsConverterFactory.create()) //<= 고질적인 에러 :JSON document was not fully consumed 해결
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
    var apiService: ApiService? = null
    fun getOcrFactory(): ApiService? {
        if (apiService == null) {
            apiService = retrofit.create(ApiService::class.java)
        }
        return apiService
    }

    private fun httpLoggingInterceptor(): HttpLoggingInterceptor {
        val interceptor = HttpLoggingInterceptor { message ->
            Log.e(
                "MyLogis :",
                message + ""
            )
        }
        return interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    companion object {
        val instance = Net()
    }
}