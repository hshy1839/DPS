package com.example.dps.mainActivity

import ApiService
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.dps.R
import com.example.dps.RetrofitClient
import com.example.dps.loginActivity.LoginActivity
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserinfoActivity : AppCompatActivity() {
    private lateinit var apiService: ApiService
    private lateinit var sharedPreferences: SharedPreferences
    private var userId: Int = 0 // userId를 정수형으로 변경

    // UI 요소들은 액티비티가 생성될 때 초기화
    private lateinit var nameText: TextView
    private lateinit var birthdayText: TextView
    private lateinit var genderText: TextView
    private lateinit var phoneNumberText: TextView
    private lateinit var roleText: TextView
    private lateinit var emailText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_userinfo)

        // UI 요소 초기화
        nameText = findViewById(R.id.nameText)
        birthdayText = findViewById(R.id.birthdayText)
        genderText = findViewById(R.id.genderText)
        phoneNumberText = findViewById(R.id.numberText)
        roleText = findViewById(R.id.roleText)
        emailText = findViewById(R.id.emailText)

        // Retrofit 인스턴스 생성
        apiService = RetrofitClient.getInstance().create(ApiService::class.java)

        // SharedPreferences 초기화
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        // 로그인 시 저장된 userId 가져오기
        userId = sharedPreferences.getInt("userId", 0) // getInt 메서드를 사용하여 가져옴

        // 사용자 정보 가져오기 요청
        fetchUserInfo()
    }

    private fun fetchUserInfo() {
        // 사용자 정보 요청 (쿼리에 userId와 sessionId를 함께 요청)
        val sessionId = sharedPreferences.getString("sessionId", "")
        val call = apiService.getUserInfo(userId, sessionId)
        call.enqueue(object : Callback<JsonArray> {
            override fun onResponse(call: Call<JsonArray>, response: Response<JsonArray>) {
                if (response.isSuccessful) {
                    val jsonArray = response.body()

                    // JSON 배열이 유효한지 확인
                    if (jsonArray != null && jsonArray.size() > 0) {
                        val jsonObject = jsonArray.get(0).asJsonObject

                        // 사용자 정보 추출
                        val email = jsonObject.get("email").asString
                        val name = jsonObject.get("name").asString
                        val birthdate = jsonObject.get("birthdate").asString
                        val gender = jsonObject.get("gender").asString
                        val phoneNumber = jsonObject.get("phoneNumber").asString
                        val role = jsonObject.get("role").asString

                        // UI에 사용자 정보 설정
                        emailText.text = email
                        nameText.text = name
                        birthdayText.text = birthdate
                        genderText.text = gender
                        phoneNumberText.text = phoneNumber
                        roleText.text = role
                    } else {
                        Log.e("EmptyData", "JsonArray is null or empty")
                    }
                } else {
                    Log.e("API", "Request failed: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<JsonArray>, t: Throwable) {
                Log.e("API", "Network error: ${t.message}")
            }
        })
    }
}
