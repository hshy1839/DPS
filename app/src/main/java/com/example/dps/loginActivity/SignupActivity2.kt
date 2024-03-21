package com.example.dps.loginActivity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.dps.R

class SignupActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup2)

        val loginBackArrowImageView = findViewById<ImageView>(R.id.back_arrow)
        loginBackArrowImageView.setOnClickListener {
            onBackPressed()
        }
        val signUpNextBtn = findViewById<Button>(R.id.signup_nextbtn)
        signUpNextBtn.setOnClickListener{
            val intent = Intent(this@SignupActivity2, SignupActivity3::class.java)
            startActivity(intent)
        }
    }
}