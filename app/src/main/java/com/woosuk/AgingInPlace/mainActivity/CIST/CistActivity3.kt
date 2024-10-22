package com.woosuk.AgingInPlace.mainActivity.CIST

import ApiService
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
import android.widget.ProgressBar
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.gson.JsonObject
import com.woosuk.AgingInPlace.CistQuestionResponse
import com.woosuk.AgingInPlace.CistResponseData
import com.woosuk.AgingInPlace.R
import com.woosuk.AgingInPlace.RetrofitClient
import com.woosuk.AgingInPlace.loginActivity.LoginActivity
import com.woosuk.AgingInPlace.mainActivity.MainActivity
import com.woosuk.AgingInPlace.mainActivity.UserInfoActivity
import com.woosuk.AgingInPlace.medication.MedicationActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CistActivity3 : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView:NavigationView
    private lateinit var apiService: ApiService
    private lateinit var sharedPreferences: SharedPreferences
    private var userId: Int = 0

    private lateinit var typeText: TextView
    private lateinit var titleText: TextView
    private lateinit var answerGroup: RadioGroup

    private lateinit var prevButton: Button
    private lateinit var LoginButton: ImageView
    private lateinit var nextButton: Button
    private lateinit var backArrow: ImageView
    private lateinit var contentLayout: LinearLayout
    private val editTextList = mutableListOf<Pair<Int, EditText>>()
    private var progressBar: ProgressBar? = null
    private var cardView: CardView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cist2)
        drawerLayout=findViewById(R.id.drawer_layout)
        // 초기화
        typeText = findViewById(R.id.cist_type)
        titleText = findViewById(R.id.cist_orientation_title)
        contentLayout = findViewById(R.id.cist_orientation_question)
        nextButton = findViewById(R.id.nextButton)
        backArrow = findViewById(R.id.back_arrow)
        LoginButton = findViewById(R.id.LoginButton)
        prevButton = findViewById(R.id.prevButton)
        navView=findViewById(R.id.nav_view)
        apiService = RetrofitClient.getInstance(this).create(ApiService::class.java)
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        userId = sharedPreferences.getInt("userId", 0)
        progressBar = findViewById(R.id.progress_bar)
        cardView = findViewById(R.id.card_view)
        fetchCistQuestions()

        // 버튼 클릭 리스너 추가
        nextButton.setOnClickListener {
            val emptyFields = editTextList.filter { pair -> pair.second.text.isEmpty() }
            if (emptyFields.isNotEmpty()) {
                // 빈 필드가 있으면 알림 표시
                Toast.makeText(this@CistActivity3, "빈 칸을 채워주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener // 함수를 종료하여 다음 단계로 진행하지 않음
            }
            showProgressBar()
            hideProgressBar()
            postCistResponses()
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
                        val filteredQuestions = questions.filter { it.type == "주의력" }
                        if (filteredQuestions.isNotEmpty()) {
                            typeText.text = filteredQuestions.first().type
                            val displayedTitles = mutableSetOf<String>()

                            // UI 업데이트
                            for (question in filteredQuestions) {
                                if (!displayedTitles.contains(question.title)) {
                                    displayedTitles.add(question.title)

                                    val questionTitle = TextView(this@CistActivity3).apply {
                                        text = question.title
                                        textSize = 18f
                                        setTextColor(android.graphics.Color.BLACK)
                                    }
                                    contentLayout.addView(questionTitle)
                                }

                                val questionText = TextView(this@CistActivity3).apply {
                                    text = question.question_text
                                    textSize = 16f
                                    setTextColor(android.graphics.Color.BLACK)
                                }
                                contentLayout.addView(questionText)

                                // EditText 추가
                                val editText = EditText(this@CistActivity3).apply {
                                    hint = "여기에 입력하세요"
                                    setTextColor(android.graphics.Color.BLACK)
                                    setHintTextColor(android.graphics.Color.GRAY)
                                    background = ContextCompat.getDrawable(this@CistActivity3, R.drawable.edittext_border)
                                }
                                contentLayout.addView(editText)

                                // 질문 ID와 EditText를 리스트에 저장
                                editTextList.add(Pair(question.id, editText))
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

    private fun postCistResponses() {
        for (pair in editTextList) {
            val questionId = pair.first
            val responseText = pair.second.text.toString()

            if (questionId != null && responseText.isNotEmpty()) {
                val cistResponseData = CistResponseData(
                    userId = userId,
                    questionId = questionId,
                    responses = responseText,
                )

                // API로 응답 전송
                val call = apiService.postCistResponse(cistResponseData)
                call.enqueue(object : Callback<JsonObject> {
                    override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                        if (response.isSuccessful) {
                            Log.d("API", "Response saved for question $questionId")
                        } else {
                            // 에러 상태 코드와 에러 메시지 출력
                            val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                            Log.e("API", "Failed to save response for question $userId $questionId $responseText. Error: $errorMessage")
                        }
                    }

                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                        Log.e("API", "Error saving response: ${t.message}")
                    }
                })
            }
            else {
                Log.e("API", "Invalid questionId or empty responseText")
            }
        }
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
    // ProgressBar를 보여줄 때
    fun showProgressBar() {
        progressBar?.visibility = View.VISIBLE
        cardView?.visibility = View.GONE // CardView 숨기기
    }

    // ProgressBar를 숨길 때
    fun hideProgressBar() {
        progressBar?.visibility = View.GONE
        cardView?.visibility = View.VISIBLE // CardView 보이기
    }
}



