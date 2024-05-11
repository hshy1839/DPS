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

class UserInfoActivity : AppCompatActivity() {
    private lateinit var apiService: ApiService
    private lateinit var sharedPreferences: SharedPreferences
    private var userId: Int = 0

    private lateinit var nameText: TextView
    private lateinit var birthdayText: TextView
    private lateinit var genderText: TextView
    private lateinit var phoneNumberText: TextView
    private lateinit var roleText: TextView
    private lateinit var emailText: TextView
    private lateinit var medicationText: TextView
    private lateinit var medicationTimeText: TextView
    private lateinit var diagnosisText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_userinfo)

        nameText = findViewById(R.id.nameText)
        birthdayText = findViewById(R.id.birthdayText)
        genderText = findViewById(R.id.genderText)
        phoneNumberText = findViewById(R.id.numberText)
        roleText = findViewById(R.id.roleText)
        emailText = findViewById(R.id.emailText)
        medicationText = findViewById(R.id.medicationText)
        medicationTimeText = findViewById(R.id.medicationTimeText)
        diagnosisText = findViewById(R.id.diagnosisText)
        apiService = RetrofitClient.getInstance(this).create(ApiService::class.java)
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        userId = sharedPreferences.getInt("userId", 0)

        fetchUserInfo(userId)
    }

    private fun fetchUserInfo(userId: Int) {
        val call = apiService.getUserInfo(userId)

        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    val jsonObject = response.body()

                    if (jsonObject != null) {
                        val email = jsonObject.get("email").asString
                        val name = jsonObject.get("name").asString
                        val birthdate = jsonObject.get("birthdate").asString
                        val gender = jsonObject.get("gender").asString
                        val phoneNumber = jsonObject.get("phoneNumber").asString
                        val role = jsonObject.get("role").asString
                        val medication = jsonObject.get("medication").asString
                        val medicationTime = jsonObject.get("medicationTime").asString
                        val diagnosis = jsonObject.get("diagnosis").asString

                        emailText.text = email
                        nameText.text = name
                        birthdayText.text = birthdate
                        genderText.text = gender
                        phoneNumberText.text = phoneNumber
                        roleText.text = role
                        medicationText.text = medication
                        medicationTimeText.text = medicationTime
                        diagnosisText.text = diagnosis
                    } else {
                        Log.e("EmptyData", "JsonObject is null")
                    }
                } else {
                    Log.e("API", "Request failed: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.e("API", "Network error: ${t.message}")
            }
        })
    }
}

