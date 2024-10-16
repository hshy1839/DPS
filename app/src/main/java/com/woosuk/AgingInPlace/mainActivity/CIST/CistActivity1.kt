package com.woosuk.AgingInPlace.mainActivity.CIST

import ApiService
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.woosuk.AgingInPlace.R
import com.woosuk.AgingInPlace.RetrofitClient
import com.google.gson.JsonObject
import com.woosuk.AgingInPlace.loginActivity.LoginActivity
import com.woosuk.AgingInPlace.mainActivity.MainActivity
import com.woosuk.AgingInPlace.medication.MedicationActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale

class CistActivity1 : AppCompatActivity() {
    private lateinit var apiService: ApiService
    private lateinit var sharedPreferences: SharedPreferences
    private var userId: Int = 0

    private lateinit var typeText: TextView
    private lateinit var titleText: TextView
    private lateinit var questionText: TextView
    private lateinit var answerText: TextView


    private lateinit var LoginButton: ImageView
    private lateinit var menuButton: ImageView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView  // navView 선언

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cist1)

        // 초기화
        navView = findViewById(R.id.nav_view)  // navView 초기화
        drawerLayout = findViewById(R.id.drawer_layout)

        val backArrow = findViewById<ImageView>(R.id.back_arrow)
        backArrow.setOnClickListener {
            onBackPressed()
        }

        titleText = findViewById(R.id.cist_orientation_title)
        questionText = findViewById(R.id.cist_orientation_question)
        answerText = findViewById(R.id.cist_orientation_answer)
        apiService = RetrofitClient.getInstance(this).create(ApiService::class.java)
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        userId = sharedPreferences.getInt("userId", 0)


        fetchCistQuestion(userId)

        LoginButton = findViewById(R.id.LoginButton)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        // 로그인 상태에 따라 로그인 버튼 가시성 조정
        if (isLoggedIn) {
            LoginButton.visibility = View.GONE
        } else {
            LoginButton.visibility = View.VISIBLE
        }
        LoginButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        // 헤더 뷰 접근
        val headerView = navView.getHeaderView(0)
        // 로그인 상태에 따라 헤더의 버튼 가시성 조정
        val menuLoginBtn: Button = headerView.findViewById(R.id.menu_loginBtn)
        menuLoginBtn.visibility = if (isLoggedIn) View.GONE else View.VISIBLE

        menuLoginBtn.setOnClickListener {
            if (!isLoggedIn) {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }

    }

    fun fetchCistQuestion(userId: Int) {
        val call = apiService.getUserInfo(userId)
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    val jsonObject = response.body()
                    if (jsonObject != null) {
                        val typeTextValue = jsonObject.get("type").asString
                        val titleTextValue = jsonObject.get("title").asString
                        val questionTextValue = jsonObject.get("question_text").asJsonArray
                        val correctAnswerTextValue = jsonObject.get("correct_answer").asString

                        // TextView에 올바르게 값 설정
                        typeText.text = typeTextValue
                        titleText.text = titleTextValue
                        answerText.text = correctAnswerTextValue // 변수 이름이 `answerText`인 것으로 가정
                        questionText.text = questionTextValue.toString() // JsonArray를 문자열로 변환
                    } else {
                        Log.e("EmptyData", "JsonObject is null")
                    }
                } else {
                    Log.e("API", "Request failed: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.e("API", "Network error: ${t.message}")
            }
        })
    }


    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun logout() {
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", false)
        editor.apply()

        // 로그아웃 완료 메시지 표시
        Toast.makeText(this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()

        // MainActivity로 이동하고 현재 액티비티 스택을 모두 지웁니다.
        val mainIntent = Intent(this, MainActivity::class.java)
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(mainIntent)
        finish()  // 현재 액티비티 종료
    }

}



