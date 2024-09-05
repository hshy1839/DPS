package com.woosuk.AgingInPlace.mainActivity.CIST

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
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
import com.woosuk.AgingInPlace.loginActivity.LoginActivity
import com.woosuk.AgingInPlace.mainActivity.MainActivity
import com.woosuk.AgingInPlace.mainActivity.UserInfoActivity
import com.woosuk.AgingInPlace.medication.MedicationActivity
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class CistActivity2 : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var navView:NavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cist2)
        sharedPreferences=getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        // View 초기화

        navView=findViewById(R.id.nav_view)

        // 기존 코드


        val loginButton=findViewById<ImageView>(R.id.loginButton)
        loginButton.setOnClickListener{
            val intent=Intent(this@CistActivity2, LoginActivity::class.java)
            startActivity(intent)
        }

        val backArrow=findViewById<ImageView>(R.id.back_arrow)
        backArrow.setOnClickListener{
            onBackPressed()
        }

        var id=getUserId()

        val isLoggedIn=sharedPreferences.getBoolean("isLoggedIn",false)
        drawerLayout=findViewById(R.id.drawer_layout)
        val navView: NavigationView =findViewById(R.id.nav_view)

        // 헤더 뷰 접근
        val headerView=navView.getHeaderView(0)

        // 로그인 상태에 따라 헤더의 버튼 가시성 조정
        loginButton.visibility=if(isLoggedIn) View.GONE else View.VISIBLE

        loginButton.setOnClickListener{
            if(!isLoggedIn){
                val intent=Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }

        val nextButton = findViewById<Button>(R.id.nextButton)

        val prevButton = findViewById<Button>(R.id.prevButton)
        // 버튼 클릭 리스너 추가
        nextButton.setOnClickListener {
            val intent = Intent(
                this@CistActivity2,
                CistActivity3::class.java
            )
            startActivity(intent)
        }
        prevButton.setOnClickListener {
            val intent = Intent(
                this@CistActivity2,
                CistActivity1::class.java
            )
            startActivity(intent)
        }
        // 토글 버튼을 추가하여 메뉴가 열리고 닫히도록 함
        val toggle= ActionBarDrawerToggle(
            this,drawerLayout,R.string.navigation_drawer_open,R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        sharedPreferences=getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        // 네비게이션 메뉴 아이템 클릭 리스너 설정
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
        Toast.makeText(applicationContext,message, Toast.LENGTH_SHORT).show()
    }
    private fun saveUserId(userId:String){
        val sharedPreferences=getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("userId",userId).apply()
    }

    private fun getUserId():Int{
        val sharedPreferences=getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("userId",0)
    }


    private fun post(apiURL:String,userId:Int):String{
        val client= OkHttpClient()

        val formBody= FormBody.Builder()
            .add("userId",userId.toString())
            .build()

        val request= Request.Builder()
            .url(apiURL)
            .post(formBody)
            .build()

        client.newCall(request).execute().use{response->
            if(!response.isSuccessful)throw IOException("Unexpected code $response")
            return response.body?.string()?:""
        }
    }
}