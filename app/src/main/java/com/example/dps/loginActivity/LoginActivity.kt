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

class ResponseDTO {

}

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val loginBackArrowImageView = findViewById<ImageView>(R.id.back_arrow)
        loginBackArrowImageView.setOnClickListener {
            // 뒤로 가기 버튼 클릭 시 MainActivity로 이동
            onBackPressed()
        }
        val signupBtn = findViewById<Button>(R.id.signup_btn)
        signupBtn.setOnClickListener {
            val intent = Intent(this@LoginActivity, SignupActivity::class.java)
            startActivity(intent)
            handleSignupDialog()
        }
        val loginBtn = findViewById<Button>(R.id.login_btn)
        loginBtn.setOnClickListener {
            handleLoginDialog()
        }

        private void handleLoginDialog() {
            View view = getLayoutInflater().inflate(R.layout.activity_login, null );
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(view).show();
            Button loginBtn = view.findViewById(R.id.login_btn);
            EditText emailEdit = view.findViewById();
            EditText emailEdit = view.findViewById();
        }
    }
}