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
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.marginTop
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.gson.JsonElement
import com.woosuk.AgingInPlace.R
import com.woosuk.AgingInPlace.RetrofitClient
import com.google.gson.JsonObject
import com.woosuk.AgingInPlace.CistQuestionResponse
import com.woosuk.AgingInPlace.R.id
import com.woosuk.AgingInPlace.loginActivity.LoginActivity
import com.woosuk.AgingInPlace.mainActivity.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.compose.ui.graphics.Color.Companion.Black as Black

class CistActivity1 : AppCompatActivity() {
    private lateinit var apiService: ApiService
    private lateinit var sharedPreferences: SharedPreferences
    private var userId: Int = 0

    private lateinit var typeText: TextView
    private lateinit var titleText: TextView
    private lateinit var questionText: TextView
    private lateinit var answerGroup: RadioGroup


    private lateinit var LoginButton: ImageView
    private lateinit var menuButton: ImageView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView  // navView 선언

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cist1)

        // 초기화
        navView = findViewById(id.nav_view)  // navView 초기화
        drawerLayout = findViewById(id.drawer_layout)

        val nextButton = findViewById<Button>(id.nextButton)
        // 버튼 클릭 리스너 추가
        nextButton.setOnClickListener {
            val intent = Intent(
                this@CistActivity1,
                CistActivity2::class.java
            )
            startActivity(intent)
        }
        val backArrow = findViewById<ImageView>(id.back_arrow)
        backArrow.setOnClickListener {
            onBackPressed()
        }

        typeText = findViewById(id.cist_type)
        titleText = findViewById(id.cist_orientation_title)
        apiService = RetrofitClient.getInstance(this).create(ApiService::class.java)
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        userId = sharedPreferences.getInt("userId", 0)


        fetchCistQuestions()

        LoginButton = findViewById(id.LoginButton)
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
        val menuLoginBtn: Button = headerView.findViewById(id.menu_loginBtn)
        menuLoginBtn.visibility = if (isLoggedIn) View.GONE else View.VISIBLE

        menuLoginBtn.setOnClickListener {
            if (!isLoggedIn) {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }

    }


    fun fetchCistQuestions() {
        val call = apiService.getCistQuestions() // 모든 질문을 가져오는 API 호출
        call.enqueue(object : Callback<List<CistQuestionResponse>> {
            override fun onResponse(call: Call<List<CistQuestionResponse>>, response: Response<List<CistQuestionResponse>>) {
                if (response.isSuccessful) {
                    val questions = response.body()
                    if (questions != null && questions.isNotEmpty()) {
                        // "지남력" 타입의 질문만 필터링
                        val filteredQuestions = questions.filter { it.type == "지남력" }

                        if (filteredQuestions.isNotEmpty()) {
                            typeText.text = filteredQuestions.first().type

                            // Set to track displayed titles
                            val displayedTitles = mutableSetOf<String>()

                            // UI 업데이트
                            val contentLayout = findViewById<LinearLayout>(id.cist_orientation_question)

                            for (question in filteredQuestions) {
                                // 질문 제목 추가 (중복 방지)
                                if (!displayedTitles.contains(question.title)) {
                                    displayedTitles.add(question.title)

                                    val questionTitle = TextView(this@CistActivity1).apply {
                                        text = question.title
                                        textSize = 18f
                                        layoutParams = LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.WRAP_CONTENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT
                                        )
                                        setTextColor(android.graphics.Color.BLACK)
                                    }

                                    contentLayout.addView(questionTitle) // 제목 추가
                                }

                                // 질문 텍스트 추가
                                val questionText = TextView(this@CistActivity1).apply {
                                    text = question.question_text
                                    textSize = 16f
                                    layoutParams = LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.WRAP_CONTENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                    )
                                    setTextColor(android.graphics.Color.BLACK)
                                }

                                val answerGroup = RadioGroup(this@CistActivity1).apply {
                                    layoutParams = LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                    )
                                }
                                // 텍스트 입력 필드 추가
                                val editText = EditText(this@CistActivity1).apply {
                                    hint = "여기에 입력하세요" // 힌트 텍스트 변경
                                    layoutParams = LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                    )
                                    setTextColor(android.graphics.Color.BLACK)

                                    setHintTextColor(android.graphics.Color.GRAY)
                                    // 검은색 테두리 추가
                                    background = ContextCompat.getDrawable(this@CistActivity1, R.drawable.edittext_border)
                                }


                                // UI에 추가
                                contentLayout.addView(questionText) // 질문 텍스트 추가
                                contentLayout.addView(editText) // EditText 추가
                                contentLayout.addView(answerGroup)
                            }
                        } else {
                            Log.e("EmptyData", "No questions found with type '지남력'")
                        }
                    } else {
                        Log.e("EmptyData", "Response is null or empty")
                    }
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                    Log.e("API", "Request failed: ${response.code()}, Error: $errorBody")
                }
            }

            override fun onFailure(call: Call<List<CistQuestionResponse>>, t: Throwable) {
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



