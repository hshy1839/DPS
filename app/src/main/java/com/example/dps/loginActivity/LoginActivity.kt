package com.example.dps.loginActivity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.dps.ApiService
import com.example.dps.R
import com.example.dps.ResponseDC
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private lateinit var retrofit: Retrofit
private lateinit var retrofitInterface: RetrofitInterface
private lateinit var BASE_URL: String

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

         retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofitInterface = retrofit.create(RetrofitInterface::class.java)

        val loginBackArrowImageView = findViewById<ImageView>(R.id.back_arrow)
        loginBackArrowImageView.setOnClickListener {
            // 뒤로 가기 버튼 클릭 시 MainActivity로 이동
            onBackPressed()
        }

        val loginBtn = findViewById<Button>(R.id.login_btn)
        val signBtn = findViewById<Button>(R.id.signup_btn)
        loginBtn.setOnClickListener {
            handleLogin()
        }

        signBtn.setOnClickListener {
            handleSignup()
        }

         fun handleSignup() {
            val view = layoutInflater.inflate(R.layout.activity_login, null)
            val builder = AlertDialog.Builder(this)
            builder.setView(view).show()
            val loginBtn = view.findViewById<Button>(R.id.login_btn)
            val emailEditview = view.findViewById<EditText>(R.id.emailEdit)
            val passwordEdit = view.findViewById<EditText>(R.id.passwordEdit)

            loginBtn.setOnClickListener {
                // handle login button click inside dialog
            }
        }

        private fun handleSignup() {
            // handle signup button click
        }

    }
}