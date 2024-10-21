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
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
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
import com.woosuk.AgingInPlace.mainActivity.UserInfoActivity
import com.woosuk.AgingInPlace.medication.MedicationActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.compose.ui.graphics.Color.Companion.Black as Black

class CistActivity3 : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView:NavigationView
    private lateinit var apiService: ApiService
    private lateinit var sharedPreferences: SharedPreferences
    private var userId: Int = 0

    private lateinit var typeText: TextView
    private lateinit var titleText: TextView
    private lateinit var answerGroup: RadioGroup

    private lateinit var LoginButton: ImageView
    private lateinit var nextButton: Button
    private lateinit var prevButton: Button
    private lateinit var backArrow: ImageView
    private lateinit var contentLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cist2)
        drawerLayout=findViewById(R.id.drawer_layout)
        // 초기화
        typeText = findViewById(R.id.cist_type)
        titleText = findViewById(R.id.cist_orientation_title)
        contentLayout = findViewById(R.id.cist_orientation_question)
        nextButton = findViewById(R.id.nextButton)
        prevButton = findViewById(R.id.prevButton)
        backArrow = findViewById(R.id.back_arrow)
        LoginButton = findViewById(R.id.LoginButton)
        navView=findViewById(R.id.nav_view)
        apiService = RetrofitClient.getInstance(this).create(ApiService::class.java)
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        userId = sharedPreferences.getInt("userId", 0)

        fetchCistQuestions()

        // 버튼 클릭 리스너 추가
        nextButton.setOnClickListener {
            val intent = Intent(this@CistActivity3, CistActivity4::class.java)
            startActivity(intent)
        }
        prevButton.setOnClickListener {
            val intent = Intent(
                this@CistActivity3,
                CistActivity2::class.java
            )
            startActivity(intent)
        }

        backArrow.setOnClickListener {
            val intent = Intent(this@CistActivity3, MainActivity::class.java)
            startActivity(intent)
        }

        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        LoginButton.visibility = if (isLoggedIn) View.GONE else View.VISIBLE
        LoginButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        val toggle= ActionBarDrawerToggle(
            this,drawerLayout,R.string.navigation_drawer_open,R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener{menuItem->
            when(menuItem.itemId){
                R.id.nav_item1 -> {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://www.aginginplaces.net/"))
                    startActivity(intent)
                }
                R.id.nav_item2 -> {
                    val intent = Intent(this, UserInfoActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_item5 -> {
                    val intent = Intent(this, MedicationActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_item4->{
                    val isLoggedIn=sharedPreferences.getBoolean("isLoggedIn",false)
                    if(isLoggedIn){
                        logout()
                    }else{
                        val intent=Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
            // 메뉴를 선택한 후에는 Drawer를 닫아줌
            drawerLayout.closeDrawer(GravityCompat.START)
            true

        }

        val nav_item4 = navView.menu.findItem(R.id.nav_item4)
        nav_item4.title = if (isLoggedIn) "로그아웃" else "로그인"

        val menuButton=findViewById<ImageView>(R.id.menuButton_heartbeat)
        menuButton.setOnClickListener{
            // 메뉴 버튼을 클릭하면 Navigation Drawer를 열도록 함
            drawerLayout.openDrawer(GravityCompat.START)
        }
        updateWelcomeMessage()

    }


    private fun fetchCistQuestions() {
        val call = apiService.getCistQuestions()
        call.enqueue(object : Callback<List<CistQuestionResponse>> {
            override fun onResponse(call: Call<List<CistQuestionResponse>>, response: Response<List<CistQuestionResponse>>) {
                if (response.isSuccessful) {
                    val questions = response.body()
                    if (questions != null && questions.isNotEmpty()) {
                        // "지남력" 타입의 질문만 필터링
                        val filteredQuestions = questions.filter { it.type == "주의력" }
                        if (filteredQuestions.isNotEmpty()) {
                            // 첫 번째 질문의 타입 설정
                            typeText.text = filteredQuestions.first().type

                            val displayedTitles = mutableSetOf<String>()

                            // UI 업데이트
                            for (question in filteredQuestions) {
                                // 제목 추가 (중복 방지)
                                if (!displayedTitles.contains(question.title)) {
                                    displayedTitles.add(question.title)

                                    val questionTitle = TextView(this@CistActivity3).apply {
                                        text = question.title
                                        textSize = 18f
                                        setTextColor(android.graphics.Color.BLACK)
                                    }

                                    contentLayout.addView(questionTitle) // 제목 추가
                                }

                                // 질문 텍스트 추가
                                val questionText = TextView(this@CistActivity3).apply {
                                    text = question.question_text
                                    textSize = 16f
                                    setTextColor(android.graphics.Color.BLACK)

                                    // Margin 설정
                                    val params = LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.WRAP_CONTENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                    ).apply {
                                        setMargins(0, 60, 0, 0) // top margin 20dp
                                    }
                                    layoutParams = params // LayoutParams 적용
                                }

                                // EditText 추가
                                val editText = EditText(this@CistActivity3).apply {
                                    hint = "여기에 입력하세요"
                                    setTextColor(android.graphics.Color.BLACK)
                                    setHintTextColor(android.graphics.Color.GRAY)
                                    background = ContextCompat.getDrawable(this@CistActivity3, R.drawable.edittext_border)

                                    // Margin 설정
                                    val params = LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                    ).apply {
                                        setMargins(0, 40, 0, 0) // top margin 20dp
                                    }
                                    layoutParams = params // LayoutParams 적용
                                }

                                // UI에 질문 텍스트와 EditText 추가
                                contentLayout.addView(questionText)
                                contentLayout.addView(editText)
                            }
                        }
                    }
                }
            }

            override fun onFailure(call: Call<List<CistQuestionResponse>>, t: Throwable) {
                Log.e("API", "Network error: ${t.message}")
            }
        })
    }


    override fun onBackPressed() {
        super.onBackPressed()
    }

    private fun logout() {
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", false)
        editor.apply()
        Toast.makeText(this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
        val mainIntent = Intent(this, MainActivity::class.java)
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(mainIntent)
        finish()
    }

    private fun updateWelcomeMessage(){
        // NavigationView의 헤더 가져오기
        val headerView=navView.getHeaderView(0)
        val welcomeTextView: TextView =headerView.findViewById(R.id.welcome_textView)

        val isLoggedIn=sharedPreferences.getBoolean("isLoggedIn",false)
        val username=sharedPreferences.getInt("userId",0)

        if(isLoggedIn){
            welcomeTextView.text="안녕하세요 $username"
        }else{
            welcomeTextView.text="로그인 후 사용해주세요"
        }
    }
}



