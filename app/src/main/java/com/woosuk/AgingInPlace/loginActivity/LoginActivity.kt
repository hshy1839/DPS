package com.woosuk.AgingInPlace.loginActivity

import ApiService
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.woosuk.AgingInPlace.LoginData
import com.woosuk.AgingInPlace.R
import com.woosuk.AgingInPlace.RetrofitClient
import com.woosuk.AgingInPlace.mainActivity.MainActivity
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale

class LoginActivity : AppCompatActivity() {
    private val retrofit = RetrofitClient.getInstance(this)
    private lateinit var apiService: ApiService
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var mainLoginButton: Button // Button으로 변경

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val backArrow = findViewById<ImageView>(R.id.back_arrow)
        backArrow.setOnClickListener {
            onBackPressed()
        }

        apiService = RetrofitClient.getInstance(this).create(ApiService::class.java)
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        checkLoginStatus()

        val idEdit = findViewById<EditText>(R.id.idEdit)
        val pwEdit = findViewById<EditText>(R.id.passwordEdit)
        val loginBtn = findViewById<Button>(R.id.login_btn) // Button으로 변경

        val signupButton = findViewById<Button>(R.id.signup_btn)
        loginBtn.setOnClickListener {
            val username = idEdit.text.toString()
            val password = pwEdit.text.toString()
            if (username.isEmpty() || password.isEmpty()) {
                // 아이디 또는 비밀번호가 비어있을 때
                Toast.makeText(this@LoginActivity, "아이디 또는 비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show()
            } else {
                // 아이디와 비밀번호가 입력되었을 때
                loginUser(username, password)
            }
        }
        signupButton.setOnClickListener {
            // SignupActivity로 이동하는 인텐트 생성
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
    }

    private fun checkLoginStatus() {
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        if (isLoggedIn) {
            // 이미 로그인되어 있다면 MainActivity로 이동
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            finish() // 로그인 화면 종료
        }
    }

    private fun loginUser(username: String, password: String) {
        val loginData = LoginData(username, password) // LoginData 객체 생성

        val call = apiService.login(loginData)
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    val jsonResponse = response.body()
                    Log.d("LoginResponse", jsonResponse.toString())
                    if (jsonResponse != null && jsonResponse.has("id")) { // 서버에서 userId를 반환하도록 변경
                    val userId = jsonResponse.get("id").asInt
                    saveUserId(userId) // userId를 저장
                        fetchUserInfo(userId)
                    // 로그인 성공
                    Toast.makeText(this@LoginActivity, "로그인 성공", Toast.LENGTH_SHORT).show()
                    // SharedPreferences에 로그인 상태 저장
                    saveLoginStatus(true)
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish() // 로그인 화면 종료
                } else {
                    // userId가 없는 경우
                    Toast.makeText(this@LoginActivity, "로그인 실패: 사용자 ID를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                }

            } else {
                // 로그인 실패
             Toast.makeText(this@LoginActivity, "로그인 실패: 아이디 또는 비밀번호가 틀립니다.", Toast.LENGTH_SHORT).show()
                                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                // 네트워크 오류 등으로 로그인 실패
                Toast.makeText(this@LoginActivity, "로그인 실패: " + t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchUserInfo(userId: Int) {
        val call = apiService.getUserInfo(userId)
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    val jsonObject = response.body()
                    if (jsonObject != null) {
                        val name = jsonObject.get("name").asString
                        val editor = sharedPreferences.edit()
                        editor.putString("name", name)
                        editor.apply()
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
    override fun onBackPressed() {
            super.onBackPressed()
    }

    private fun saveUserId(userId: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt("userId", userId)
        editor.apply()
    }

    private fun saveLoginStatus(isLoggedIn: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", isLoggedIn)
        editor.apply() // commit() 대신 apply()를 사용하여 비동기로 저장
    }



}
