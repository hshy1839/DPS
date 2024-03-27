package com.example.dps.mainActivity

import ApiService
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.dps.R
import com.example.dps.RetrofitClient
import com.example.dps.UserData
import com.example.dps.loginActivity.LoginActivity
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat

class UserinfoActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private val retrofit = RetrofitClient.getInstance()
    private lateinit var apiService: ApiService
    private lateinit var LoginButton: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_userinfo)

        navView = findViewById(R.id.nav_view)
        drawerLayout = findViewById(R.id.drawer_layout)
        LoginButton = findViewById(R.id.LoginButton)
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        if (!isLoggedIn) {
            AlertDialog.Builder(this@UserinfoActivity)
                .setMessage("로그인이 필요합니다. 로그인 해주세요.")
                .setPositiveButton("확인") { _, _ ->
                    val intent = Intent(this@UserinfoActivity, LoginActivity::class.java)
                    startActivity(intent)
                }
                .setCancelable(false)
                .show()
        } else {
            LoginButton.visibility = View.GONE
        }

        val genderText = findViewById<TextView>(R.id.genderText)
        val nameText = findViewById<TextView>(R.id.nameText)
        val roleText = findViewById<TextView>(R.id.roleText)
        val numberText = findViewById<TextView>(R.id.numberText)
        val birthdayText = findViewById<TextView>(R.id.birthdayText)

        apiService = retrofit.create(ApiService::class.java)
        val call = apiService.getUserInfo()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")

        call.enqueue(object : Callback<List<UserData>> {
            override fun onResponse(call: Call<List<UserData>>, response: Response<List<UserData>>) {
                if (response.isSuccessful) {
                    val userList = response.body() // 사용자 정보 리스트
                    if (userList.isNullOrEmpty()) {
                        // 사용자 정보가 없는 경우 처리
                        Log.e("API", "User data list is empty")
                    } else {
                        val userData = userList[0] // 첫 번째 사용자 정보를 가져옴
                        val formattedDate = dateFormat.format(userData.birthdate)
                        genderText.text = userData.gender
                        nameText.text = userData.name
                        roleText.text = userData.role
                        numberText.text = userData.phoneNumber
                        birthdayText.text = formattedDate
                    }
                } else {
                    // 요청이 실패한 경우 처리
                    Log.e("API", "Request failed: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<UserData>>, t: Throwable) {
                Log.e("UserinfoActivity", "Failed to fetch user info", t)
            }
        })
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_item1 -> {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.aginginplaces.net/"))
                    startActivity(intent)
                }
                R.id.nav_item2 -> {
                    menushowToast("고객센터 이동 버튼")
                }
                R.id.nav_item3 -> {
                    menushowToast("설정 버튼")
                }
                R.id.nav_item4 -> {
                    logout()
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    private fun logout() {
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", false)
        editor.apply()

        val intent = Intent(this@UserinfoActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun menushowToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }
}
