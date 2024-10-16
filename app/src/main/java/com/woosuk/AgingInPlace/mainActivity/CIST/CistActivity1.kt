package com.woosuk.AgingInPlace.mainActivity.CIST

import ApiService
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.gson.JsonElement
import com.woosuk.AgingInPlace.R
import com.woosuk.AgingInPlace.RetrofitClient
import com.google.gson.JsonObject
import com.woosuk.AgingInPlace.CistQuestionResponse
import com.woosuk.AgingInPlace.loginActivity.LoginActivity
import com.woosuk.AgingInPlace.mainActivity.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

        val nextButton = findViewById<Button>(R.id.nextButton)
        // 버튼 클릭 리스너 추가
        nextButton.setOnClickListener {
            val intent = Intent(
                this@CistActivity1,
                CistActivity2::class.java
            )
            startActivity(intent)
        }
        val backArrow = findViewById<ImageView>(R.id.back_arrow)
        backArrow.setOnClickListener {
            onBackPressed()
        }

        typeText = findViewById(R.id.cist_type)
        titleText = findViewById(R.id.cist_orientation_title)
        questionText = findViewById(R.id.cist_orientation_question)
        answerText = findViewById(R.id.cist_orientation_answer)
        apiService = RetrofitClient.getInstance(this).create(ApiService::class.java)
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        userId = sharedPreferences.getInt("userId", 0)


        fetchCistQuestions()

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


    fun fetchCistQuestions() {
        val call = apiService.getCistQuestions()
        call.enqueue(object : Callback<JsonElement> {
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                if (response.isSuccessful) {
                    val jsonResponse = response.body()
                    if (jsonResponse != null && jsonResponse.isJsonArray) {
                        val questions = jsonResponse.asJsonArray
                        for (jsonElement in questions) {
                            val question = jsonElement.asJsonObject
                            // 질문 데이터 추출
                            val typeTextValue = question.get("type").asString
                            val titleTextValue = question.get("title").asString
                            val questionTextValue = question.get("question_text").asString
                            val correctAnswerTextValue = question.get("correct_answer").asString

                            // UI 업데이트
                            typeText.text = typeTextValue
                            titleText.text = titleTextValue
                            questionText.text = questionTextValue

                            Log.d("API Response", "Type: $typeTextValue, Title: $titleTextValue, Question: $questionTextValue, Correct Answer: $correctAnswerTextValue")
                        }
                    } else {
                        Log.e("EmptyData", "Response is null or not an array")
                    }
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                    Log.e("API", "Request failed: ${response.code()}, Error: $errorBody")
                }
            }

            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
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



