// LoginActivity.kt

package com.example.dps.loginActivity

import ApiService
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dps.LoginData
import com.example.dps.R
import com.example.dps.RetrofitClient
import com.example.dps.mainActivity.MainActivity
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var apiService: ApiService
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        apiService = RetrofitClient.getInstance(this).create(ApiService::class.java)

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        checkLoginStatus()

        val idEdit = findViewById<EditText>(R.id.idEdit)
        val pwEdit = findViewById<EditText>(R.id.passwordEdit)
        val loginBtn = findViewById<Button>(R.id.login_btn)

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
    }

    private fun checkLoginStatus() {
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        if (isLoggedIn) {
            // 이미 로그인되어 있다면 MainActivity로 이동
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
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

                    if (jsonResponse != null && jsonResponse.has("userId")) { // 서버에서 userId를 반환하도록 변경
                        val userId = jsonResponse.get("userId").asInt
                        saveUserId(userId) // userId를 저장

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

    private fun saveUserId(userId: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt("userId", userId)
        editor.apply()
    }

    private fun saveLoginStatus(isLoggedIn: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", isLoggedIn)
        editor.apply()
    }
}
