package com.example.dps.loginActivity

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.dps.R
import com.example.dps.mainActivity.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private lateinit var retrofit: Retrofit
private lateinit var retrofitInterface: RetrofitInterface
private var BASE_URL = "http://127.0.0.1:5000"

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


    }
    val loginBtn = findViewById<Button>(R.id.login_btn)
    val signBtn = findViewById<Button>(R.id.signup_btn)

    fun handleLogin() {
        val view = layoutInflater.inflate(R.layout.activity_login, null)
        val builder = AlertDialog.Builder(this)
        builder.setView(view).show()
        val passwordEdit = view.findViewById<EditText>(R.id.passwordEdit)
        val idEdit = view.findViewById<EditText>(R.id.idEdit)

        loginBtn.setOnClickListener {
            val map = HashMap<String, String>()
            map["id"] = idEdit.text.toString()
            map["password"] = passwordEdit.text.toString()

            val call = retrofitInterface.executeLogin(map)

            call.enqueue(object : Callback<LoginResult> {
                override fun onResponse(call: Call<LoginResult>, response: Response<LoginResult>
                ) {
                    if (response.code() == 200) {
                        val result: LoginResult? = response.body()
                        val builder1: AlertDialog.Builder = AlertDialog.Builder(this@LoginActivity)
                        builder1.setTitle(result?.name);
                        builder1.setMessage(result?.email);
                        builder1.show()
                    } else if(response.code() == 400) {
                        Toast.makeText(this@LoginActivity, "잘못된 계정입니다.", Toast.LENGTH_LONG).show()
                    }

                }

                override fun onFailure(call: Call<LoginResult>, t: Throwable) {
                    Toast.makeText(this@LoginActivity, t.message, Toast.LENGTH_LONG).show()
                }
            })
        }

    }

    fun handleSignup() {
        signBtn.setOnClickListener {
            val map = HashMap<String, String>()
            val view = layoutInflater.inflate(R.layout.activity_signup, null)
            val builder = AlertDialog.Builder(this)
            builder.setView(view).show()
            val idEdit = findViewById<EditText>(R.id.idEdit)
            val password = findViewById<EditText>(R.id.passwordEdit)
            val email = findViewById<EditText>(R.id.emailEdit)
            val phoneNumber = findViewById<EditText>(R.id.phoneNumberEdit)

            map["id"] = idEdit.text.toString()
            map["password"] = password.text.toString()
            map["email"] = email.text.toString()
            map["phoneNumber"] = phoneNumber.text.toString()

            val call: Call<Void> = retrofitInterface.executeSignup(map)
            call.enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>)
                {
                    if (response.code() == 200) {
                        Toast.makeText(this@LoginActivity, "Signed up successfully", Toast.LENGTH_LONG).show()
                    } else if (response.code() == 400) {
                    Toast.makeText(this@LoginActivity, "Already registered", Toast.LENGTH_LONG).show()
                }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(this@LoginActivity, t.message, Toast.LENGTH_LONG).show()
                }
            })

        }

    }
}

