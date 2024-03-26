package com.example.dps.loginActivity

import ApiService
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dps.LoginData
import com.example.dps.R
import com.example.dps.RetrofitClient
import com.example.dps.mainActivity.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private val retrofit = RetrofitClient.getInstance()
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val loginBackArrowImageView = findViewById<ImageView>(R.id.back_arrow)
        loginBackArrowImageView.setOnClickListener {
            onBackPressed()
        }
        val signupBtn = findViewById<Button>(R.id.signup_btn)
        signupBtn.setOnClickListener {
            val intent = Intent(this@LoginActivity, SignupActivity::class.java)
            startActivity(intent)
        }

        apiService = RetrofitClient.getInstance().create(ApiService::class.java)

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

    private fun loginUser(username: String, password: String) {
        val call = apiService.login(LoginData(username, password))
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    // 로그인 성공
                    Toast.makeText(this@LoginActivity, "로그인 성공", Toast.LENGTH_SHORT).show()
                    // 로그인 성공 시 메인 화면으로 이동
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish() // 로그인 화면 종료
                } else {
                    // 로그인 실패
                    Toast.makeText(this@LoginActivity, "아이디 또는 비밀번호가 틀립니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                // 네트워크 오류 등으로 로그인 실패
                Toast.makeText(this@LoginActivity, "로그인 실패: " + t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }
}
