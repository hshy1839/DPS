package com.example.dps

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val loginBackArrowImageView = findViewById<ImageView>(R.id.login_back_arrow)
        loginBackArrowImageView.setOnClickListener {
            // 뒤로 가기 버튼 클릭 시 MainActivity로 이동
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
        }
        val signupBtn = findViewById<Button>(R.id.signup_btn)
        signupBtn.setOnClickListener {
            val intent = Intent(this@LoginActivity, SignupActivity::class.java)
            startActivity(intent)
        }
    }
}