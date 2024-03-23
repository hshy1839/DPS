package com.example.dps.loginActivity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.dps.R
import com.example.dps.mainActivity.MainActivity

class SignupActivity3 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup3)

        fun startMainActivity() {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }//메인페이지로 가면 초기화 시키는 함수

       val signUpNextBtn = findViewById<Button>(R.id.signup_nextbtn)
        signUpNextBtn.setOnClickListener{
            val intent = Intent(this@SignupActivity3, MainActivity::class.java)
            startActivity(intent)
            startMainActivity()
        }
        val email = findViewById<EditText>(R.id.emailEdit)
        val name = findViewById<EditText>(R.id.nameEdit)
        val phonenumber = findViewById<EditText>(R.id.phoneNumberEdit)
        val birthday = findViewById<EditText>(R.id.birthdayEdit)


    }
}