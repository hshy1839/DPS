package com.woosuk.AgingInPlace.medication

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import com.woosuk.AgingInPlace.Medication
import com.woosuk.AgingInPlace.R
import com.woosuk.AgingInPlace.loginActivity.LoginActivity
import com.woosuk.AgingInPlace.mainActivity.MainActivity
import com.woosuk.AgingInPlace.mainActivity.UserInfoActivity
import com.woosuk.AgingInPlace.medication.MedicationAdapter
import com.woosuk.AgingInPlace.receiver.MedicationAlarmReceiver
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.util.Calendar

class MedicationActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MedicationAdapter
    private lateinit var navView: NavigationView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var sharedPreferences: SharedPreferences

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medication)

        // RecyclerView 초기화
        recyclerView = findViewById(R.id.medicationRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = MedicationAdapter(emptyList(), emptyList())
        recyclerView.adapter = adapter

        // NavigationView 초기화
        navView = findViewById(R.id.nav_view)

        // 로그인 버튼 클릭 리스너 설정
        val loginButton = findViewById<ImageView>(R.id.loginButton)
        loginButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        // 뒤로가기 버튼 클릭 리스너 설정
        val backArrow = findViewById<ImageView>(R.id.back_arrow)
        backArrow.setOnClickListener {
            onBackPressed()
        }

        // SharedPreferences에서 userId 가져오기
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getInt("userId", 0)
        Log.i("userId: ","${userId}")
        // userId가 null이 아닌 경우 서버에 요청
        if (userId != null) {
            fetchMedications(userId)
        } else {
            Log.e("MedicationActivity", "User ID is null")
        }

        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        drawerLayout = findViewById(R.id.drawer_layout)

        // 헤더 뷰 접근 및 로그인 상태에 따라 헤더의 버튼 가시성 조정
        val headerView = navView.getHeaderView(0)
        loginButton.visibility = if (isLoggedIn) View.GONE else View.VISIBLE

        loginButton.setOnClickListener {
            if (!isLoggedIn) {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }

        // 드로어 토글 설정
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

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

        // 메뉴 버튼 클릭 리스너 설정
        val menuButton = findViewById<ImageView>(R.id.menuButton)
        menuButton.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // 웰컴 메시지 업데이트
        updateWelcomeMessage()

        fetchMedicationTime()

        val editMedicationTimeButton = findViewById<Button>(R.id.editMedicationTimeButton)
        editMedicationTimeButton.setOnClickListener {
            showTimePickerDialog()
        }

        // 수정 버튼 클릭 리스너 설정
        val modifyButton = findViewById<Button>(R.id.myinfo_modifyBtn)
        modifyButton.setOnClickListener {
            updateAlarmTimeInDatabase()
        }

    }

    private fun showTimePickerDialog() {
        val currentTime = sharedPreferences.getString("alarm_time", "12:00") ?: "12:00"
        val timeParts = currentTime.split(":")
        val hour = timeParts[0].toInt()
        val minute = timeParts[1].toInt()

        val timePickerDialog = TimePickerDialog(this, { _, selectedHour, selectedMinute ->
            val newAlarmTime = String.format("%02d:%02d", selectedHour, selectedMinute)
            saveAlarmTimeToSharedPreferences(newAlarmTime)
            displayAlarmTime(newAlarmTime)
        }, hour, minute, true)

        timePickerDialog.show()
    }

    private fun fetchMedicationTime() {
        val alarmTime = sharedPreferences.getString("alarm_time", "12:00") ?: "12:00"

        // "HH:mm:ss" 형식의 시간을 "HH:mm" 형식으로 변환
        val formattedAlarmTime = alarmTime.substring(0, 5)

        runOnUiThread {
            displayAlarmTime(formattedAlarmTime)
        }
    }

    private fun saveAlarmTimeToSharedPreferences(alarmTime: String) {
        val editor = sharedPreferences.edit()
        editor.putString("alarm_modify_time", alarmTime)
        editor.apply()
    }

    private fun displayAlarmTime(alarmTime: String) {
        val medicationTimeText = findViewById<TextView>(R.id.medicationTimeText)
        medicationTimeText.text = alarmTime

    }


    private fun updateWelcomeMessage() {
        val headerView = navView.getHeaderView(0)
        val welcomeTextView: TextView = headerView.findViewById(R.id.welcome_textView)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        val username = sharedPreferences.getInt("userId", 0)

        if (isLoggedIn) {
            welcomeTextView.text = "안녕하세요 $username"
        } else {
            welcomeTextView.text = "로그인 후 사용해주세요"
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


    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    private fun fetchMedications(userId: Int) {
        val client = OkHttpClient()
        val requestBody = FormBody.Builder()
            .add("userId", userId.toString())
            .build()

        val request = Request.Builder()
            .url("http://localhost:8080/medications")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                runOnUiThread {
                    Log.e("MedicationActivity", "Failed to fetch data from server: ${e.message}")
                    showToast("서버와의 연결에 실패했습니다. 인터넷 연결을 확인하세요.")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    response.body?.let { responseBody ->
                        // 서버 응답을 Medication 객체로 변환
                        val medicationsResponse = Gson().fromJson(responseBody.string(), Array<Medication>::class.java).toList()

                        // Unknown Disease를 제외한 질병 리스트를 필터링
                        val medicationNames = medicationsResponse.map { it.medications }
                        val diseaseNames = medicationsResponse.map { it.disease }
                            .filter { it != "Unknown Disease" } // "Unknown Disease" 값을 제외

                        Log.i("MedicationActivity", "Received medications: $medicationNames, $diseaseNames")
                        runOnUiThread {
                            displayMedications(medicationNames, diseaseNames)
                        }
                    } ?: run {
                        runOnUiThread {
                            showToast("서버로부터 데이터를 받지 못했습니다.")
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    runOnUiThread {
                        Log.e("MedicationActivity", "Error processing server response: ${e.message}")
                        showToast("데이터를 처리하는 중 오류가 발생했습니다.")
                    }
                }
            }
        })
    }

    private fun displayMedications(medicationNames: List<String>, diseaseNames: List<String>) {
        adapter = MedicationAdapter(medicationNames, diseaseNames)
        recyclerView.adapter = adapter
    }

    private fun updateAlarmTimeInDatabase() {
        val alarmTime = sharedPreferences.getString("alarm_modify_time", "12:00") ?: "12:00"
        val userId = sharedPreferences.getInt("userId", 0)

        val json = """
            {
                "userId": $userId,
                "alarmTime": "$alarmTime"
            }
        """.trimIndent()

        val requestBody = json.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        val client = OkHttpClient()
        val request = Request.Builder()
            .url("http://localhost:8080/medications/updateAlarmTime")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Log.e("MedicationActivity", "Failed to update data on server: ${e.message}")
                    showToast("서버와의 연결에 실패했습니다. 다시 시도하세요.")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    runOnUiThread {
                        showToast("알람 시간이 업데이트되었습니다.")

                        val editor = sharedPreferences.edit()
                        editor.putString("alarm_time", alarmTime)
                        editor.apply()

                        navigateToMainActivity()
                    }
                } else {
                    runOnUiThread {
                        showToast("서버로부터 유효한 응답을 받지 못했습니다.")
                    }
                }
            }
        })
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
