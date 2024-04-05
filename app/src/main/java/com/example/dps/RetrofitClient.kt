package com.example.dps

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://172.16.3.22:5000"
    private var retrofit: Retrofit? = null

    fun getInstance(): Retrofit {
        if (retrofit == null) {
            val gson = GsonBuilder().setLenient().create() // Gson 객체 생성 및 설정
            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson)) // Gson 객체를 Retrofit에 추가
                .build()
        }
        return retrofit!!
    }
}

