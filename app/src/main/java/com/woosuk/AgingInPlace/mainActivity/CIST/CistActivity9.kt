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


class CistActivity9 : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView:NavigationView
    private lateinit var apiService: ApiService
    private lateinit var sharedPreferences: SharedPreferences
    private var userId: Int = 0

    private lateinit var prevButton: Button
    private lateinit var LoginButton: ImageView
    private lateinit var mainButton: Button
    private lateinit var backArrow: ImageView
    private lateinit var contentLayout: LinearLayout
    private var progressBar: ProgressBar? = null
    private var cardView: CardView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cist_result)
        drawerLayout=findViewById(R.id.drawer_layout)
        // 초기화
        backArrow = findViewById(R.id.back_arrow)
        LoginButton = findViewById(R.id.LoginButton)
        prevButton = findViewById(R.id.prevButton)
        mainButton = findViewById(R.id.mainButton)
        navView=findViewById(R.id.nav_view)
        apiService = RetrofitClient.getInstance(this).create(ApiService::class.java)
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        userId = sharedPreferences.getInt("userId", 0)

        // 버튼 클릭 리스너 추가
        mainButton.setOnClickListener {
            val intent = Intent(this@CistActivity9, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }

        prevButton.setOnClickListener {
            val intent = Intent(
                this@CistActivity9,
                CistActivity8::class.java
            )
            startActivity(intent)
        }


        backArrow.setOnClickListener {
            val intent = Intent(this@CistActivity9, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
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



