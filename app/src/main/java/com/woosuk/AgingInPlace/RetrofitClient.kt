package com.woosuk.AgingInPlace

import ApiService
import android.content.Context
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://15.164.57.70"
    private var retrofit: Retrofit? = null
    fun getInstance(context: Context): Retrofit {
        if (retrofit == null) {
            val client = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val requestBuilder = chain.request().newBuilder()
                    val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                    val userId = sharedPreferences.getInt("userId", -1)
                    if (userId != -1) {
                        requestBuilder.addHeader("Cookie", "userId=$userId")
                    }
                    val request = requestBuilder.build()
                    chain.proceed(request)
                }
                .build()

            val gson = GsonBuilder().setLenient().create()
            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        }
        return retrofit!!
    }
}
//object RetrofitInstance {
//    private val retrofit by lazy {
//        Retrofit.Builder()
//            .baseUrl("http://3.39.236.95:8080/") // 서버의 기본 URL
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//    }
//
//    val api: ApiService by lazy {
//        retrofit.create(ApiService::class.java)
//    }
//}