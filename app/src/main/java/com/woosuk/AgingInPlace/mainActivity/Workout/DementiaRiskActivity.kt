package com.woosuk.AgingInPlace.mainActivity.Workout

import android.annotation.SuppressLint
import android.content.Context

import com.woosuk.AgingInPlace.loginActivity.LoginActivity

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
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
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout

import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.material.navigation.NavigationView
import com.woosuk.AgingInPlace.R

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request

import org.json.JSONArray
import org.json.JSONException

import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DementiaRiskActivity :AppCompatActivity(){

    private lateinit var drawerLayout:DrawerLayout
    private lateinit var sharedPreferences:SharedPreferences
    private lateinit var navView:NavigationView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState:Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dementia_risk)
        sharedPreferences=getSharedPreferences("MyPrefs",Context.MODE_PRIVATE)
        // View 초기화

        navView=findViewById(R.id.nav_view)

        // 기존 코드


        val loginButton=findViewById<ImageView>(R.id.loginButton)
        loginButton.setOnClickListener{
            val intent=Intent(this@DementiaRiskActivity,LoginActivity::class.java)
            startActivity(intent)
        }

        val backArrow=findViewById<ImageView>(R.id.back_arrow)
        backArrow.setOnClickListener{
            onBackPressed()
        }

        var id=getUserId()

        val isLoggedIn=sharedPreferences.getBoolean("isLoggedIn",false)
        drawerLayout=findViewById(R.id.drawer_layout)
        val navView:NavigationView=findViewById(R.id.nav_view)

        // 헤더 뷰 접근
        val headerView=navView.getHeaderView(0)

        // 로그인 상태에 따라 헤더의 버튼 가시성 조정
        loginButton.visibility=if(isLoggedIn)View.GONE else View.VISIBLE

        loginButton.setOnClickListener{
            if(!isLoggedIn){
                val intent=Intent(this,LoginActivity::class.java)
                startActivity(intent)
            }
        }

        // 토글 버튼을 추가하여 메뉴가 열리고 닫히도록 함
        val toggle=ActionBarDrawerToggle(
            this,drawerLayout,R.string.navigation_drawer_open,R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        sharedPreferences=getSharedPreferences("MyPrefs",Context.MODE_PRIVATE)

        // 네비게이션 메뉴 아이템 클릭 리스너 설정
        navView.setNavigationItemSelectedListener{menuItem->
            when(menuItem.itemId){
                R.id.nav_item1->{
                    // Menu 1 선택 시의 동작
                    val intent=Intent(Intent.ACTION_VIEW,Uri.parse("http://www.aginginplaces.net/"))
                    startActivity(intent)
                }
                R.id.nav_item2->{
                    // Menu 2 선택 시의 동작
                    showToast("고객센터 이동 버튼")
                }
                R.id.nav_item3->{
                    // Menu 3 선택 시의 동작
                    showToast("설정 버튼")
                }
                R.id.nav_item4->{
                    val isLoggedIn=sharedPreferences.getBoolean("isLoggedIn",false)
                    if(isLoggedIn){
                        logout()
                    }else{
                        val intent=Intent(this,LoginActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
            // 메뉴를 선택한 후에는 Drawer를 닫아줌
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        val menuButton=findViewById<ImageView>(R.id.menuButton_heartbeat)
        menuButton.setOnClickListener{
            // 메뉴 버튼을 클릭하면 Navigation Drawer를 열도록 함
            drawerLayout.openDrawer(GravityCompat.START)
        }
        updateWelcomeMessage()
    }
    private fun updateWelcomeMessage(){
        // NavigationView의 헤더 가져오기
        val headerView=navView.getHeaderView(0)
        val welcomeTextView:TextView=headerView.findViewById(R.id.welcome_textView)

        val isLoggedIn=sharedPreferences.getBoolean("isLoggedIn",false)
        val username=sharedPreferences.getInt("userId",0)

        if(isLoggedIn){
            welcomeTextView.text="안녕하세요 $username"
        }else{
            welcomeTextView.text="로그인 후 사용해주세요"
        }
    }

    private fun logout(){
        val editor=sharedPreferences.edit()
        editor.putBoolean("isLoggedIn",false)
        editor.commit()

        // 로그인 화면으로 이동합니다.
        val intent=Intent(this,LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onBackPressed(){
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            // 만약 Navigation Drawer가 열려 있다면, 닫기
            drawerLayout.closeDrawer(GravityCompat.START)
        }else{
            // 그렇지 않으면 기본 동작 수행
            super.onBackPressed()
        }
    }

    private fun showToast(message:String){
        Toast.makeText(applicationContext,message,Toast.LENGTH_SHORT).show()
    }
    private fun saveUserId(userId:String){
        val sharedPreferences=getSharedPreferences("AppPrefs",Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("userId",userId).apply()
    }

    private fun getUserId():Int{
        val sharedPreferences=getSharedPreferences("MyPrefs",Context.MODE_PRIVATE)
        return sharedPreferences.getInt("userId",0)
    }


    private fun post(apiURL:String,userId:Int):String{
        val client=OkHttpClient()

        val formBody=FormBody.Builder()
            .add("userId",userId.toString())
            .build()

        val request=Request.Builder()
            .url(apiURL)
            .post(formBody)
            .build()

        client.newCall(request).execute().use{response->
            if(!response.isSuccessful)throw IOException("Unexpected code $response")
            return response.body?.string()?:""
        }
    }
}

