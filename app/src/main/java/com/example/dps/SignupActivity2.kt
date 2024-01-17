package com.example.dps

import SignupActivity1
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class SignupActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup2)

        val loginBackArrowImageView = findViewById<ImageView>(R.id.back_arrow)
        loginBackArrowImageView.setOnClickListener {
            // 뒤로 가기 버튼 클릭 시 MainActivity로 이동
            val intent = Intent(this@SignupActivity2, SignupActivity1::class.java)
            startActivity(intent)
        }
    }
}