package com.example.dps.loginActivity

import ApiService
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.dps.FindPwData_login
import com.example.dps.FindPwResponse_login
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


//        loginBtn.setOnClickListener {
//            val username = idEdit.text.toString()
//            val password = pwEdit.text.toString()
//            loginUser(username, password)
//        }
//    }

//    private fun loginUser(username: String, password: String) {
//        if (username.isEmpty() || password.isEmpty()) {
//            Toast.makeText(this@LoginActivity, "아이디와 비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        // LoginData 객체 생성
//        val loginData = LoginData(username, password)
//
//        try {
//            apiService.loginUser(loginData).enqueue(object : Callback<String> {
//                override fun onResponse(call: Call<String>, response: Response<String>) {
//                    if (response.isSuccessful) {
//                        val responseBody = response.body()
//                        if (responseBody != null) {
//                            // 로그인 성공
//                            Toast.makeText(this@LoginActivity, "로그인 성공", Toast.LENGTH_SHORT).show()
//                            // 여기에 추가하려는 코드를 작성하세요
//                        } else {
//                            Toast.makeText(this@LoginActivity, "아이디 또는 비밀번호를 확인하세요.", Toast.LENGTH_SHORT).show()
//                        }
//                    } else {
//                        Toast.makeText(this@LoginActivity, "서버 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
//                    }
//                }
//
//                override fun onFailure(call: Call<String>, t: Throwable) {
//                    Toast.makeText(this@LoginActivity, "네트워크 오류", Toast.LENGTH_SHORT).show()
//                }
//            })
//        } catch (error: Throwable) {
//            error.printStackTrace()
//            Toast.makeText(this@LoginActivity, "아이디 또는 비밀번호를 확인하세요.", Toast.LENGTH_SHORT).show()
//        }
    }
}
