package com.woosuk.AgingInPlace.mainActivity

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
import com.woosuk.AgingInPlace.medication.MedicationActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale

class UserInfoActivity : AppCompatActivity() {
    private lateinit var apiService: ApiService
    private lateinit var sharedPreferences: SharedPreferences
    private var userId: Int = 0

    private lateinit var nameText: TextView
    private lateinit var birthdayText: TextView
    private lateinit var genderText: TextView
    private lateinit var phoneNumberText: TextView
    private lateinit var roleText: TextView
    private lateinit var emailText: TextView
    private lateinit var LoginButton : ImageView
    private lateinit var menuButton: ImageView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView  // navView 선언

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_userinfo)

        // 초기화
        navView = findViewById(R.id.nav_view)  // navView 초기화
        drawerLayout = findViewById(R.id.drawer_layout)

        val backArrow = findViewById<ImageView>(R.id.back_arrow)
        backArrow.setOnClickListener {
            onBackPressed()
        }

        nameText = findViewById(R.id.nameText)
        birthdayText = findViewById(R.id.birthdayText)
        genderText = findViewById(R.id.genderText)
        phoneNumberText = findViewById(R.id.numberText)
        roleText = findViewById(R.id.roleText)
        emailText = findViewById(R.id.emailText)
        apiService = RetrofitClient.getInstance(this).create(ApiService::class.java)
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        userId = sharedPreferences.getInt("userId", 0)
        val name = sharedPreferences.getString("name", "")

        fetchUserInfo(userId)

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


        // 네비게이션 메뉴 아이템 클릭 리스너 설정
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
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
                R.id.nav_item4 -> {
                    if (isLoggedIn) {
                        logout()
                    } else {
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
        val nav_item4 = navView.menu.findItem(R.id.nav_item4)
        nav_item4.title = if (isLoggedIn) "로그아웃" else "로그인"

        menuButton = findViewById(R.id.menuButton)
        menuButton.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        updateWelcomeMessage()  // navView가 초기화된 이후에 호출
    }




    private fun fetchUserInfo(userId: Int) {
        val call = apiService.getUserInfo(userId)
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    val jsonObject = response.body()
                    if (jsonObject != null) {
                        val email = jsonObject.get("email").asString
                        val name = jsonObject.get("name").asString
                        val birthdate = jsonObject.get("birthdate").asString
                        val gender = jsonObject.get("gender").asString
                        val phoneNumber = jsonObject.get("phoneNumber").asString
                        val role = jsonObject.get("role").asString

                        val originalFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val date = originalFormat.parse(birthdate)
                        val formattedBirthdate = originalFormat.format(date)

                        emailText.text = email
                        nameText.text = name
                        birthdayText.text = birthdate.substring(0, 10)
                        genderText.text = gender
                        phoneNumberText.text = phoneNumber
                        roleText.text = role
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

    private fun updateWelcomeMessage() {
        // NavigationView의 헤더 가져오기
        val headerView = navView.getHeaderView(0)
        val welcomeTextView: TextView = headerView.findViewById(R.id.welcome_textView)

        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        val name = sharedPreferences.getString("name", "")

        if (isLoggedIn) {
            welcomeTextView.text = "안녕하세요 $name"
        } else {
            welcomeTextView.text = "로그인 후 사용해주세요"
        }
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


    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }
}


