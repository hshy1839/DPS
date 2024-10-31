package com.woosuk.AgingInPlace.mainActivity.Calorie

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

import com.woosuk.AgingInPlace.R
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.material.navigation.NavigationView
import com.woosuk.AgingInPlace.mainActivity.MainActivity
import com.woosuk.AgingInPlace.mainActivity.UserInfoActivity
import com.woosuk.AgingInPlace.medication.MedicationActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.internal.format
import org.json.JSONArray
import org.json.JSONException
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Date
import java.util.Locale


class CalorieActivity : AppCompatActivity() {
    private lateinit var navView: NavigationView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var stepBarChart: BarChart
    private lateinit var calorieBarChart: BarChart
    private lateinit var calorie_text: TextView
    private lateinit var calorie_icon: ImageView
    private lateinit var calorie_message: TextView
    private lateinit var sharedPreferences: SharedPreferences

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calorie)
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        // View 초기화
        calorie_text = findViewById(R.id.calorie_text)
        calorie_icon = findViewById(R.id.calorie_icon)
        calorie_message = findViewById(R.id.calorie_message)
        navView = findViewById(R.id.nav_view)
        drawerLayout = findViewById(R.id.drawer_layout)


        val loginButton = findViewById<ImageView>(R.id.loginButton)
        loginButton.setOnClickListener {
            val intent = Intent(this@CalorieActivity, LoginActivity::class.java)
            startActivity(intent)
        }

        val backArrow = findViewById<ImageView>(R.id.back_arrow)
        backArrow.setOnClickListener {
            onBackPressed()
        }

        stepBarChart = findViewById<BarChart>(R.id.stepBarChart)
        setupStepBarChart(stepBarChart)


         calorieBarChart = findViewById<BarChart>(R.id.calorieBarChart)
        setupCalorieBarChart(calorieBarChart)

        var id = getUserId()
        fetchDataFromApi("http://3.39.236.95:8080/chart/steps", id)
        fetchDataFromApiCalories("http://3.39.236.95:8080/chart/calories", id)


        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        val navView: NavigationView = findViewById(R.id.nav_view)
        // 헤더 뷰 접근
        val headerView = navView.getHeaderView(0)

        // 로그인 상태에 따라 헤더의 버튼 가시성 조정
        loginButton.visibility = if (isLoggedIn) View.GONE else View.VISIBLE

        loginButton.setOnClickListener {
            if (!isLoggedIn) {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }

        // 토글 버튼을 추가하여 메뉴가 열리고 닫히도록 함
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()



        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

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
                    val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
                    if (isLoggedIn) {
                        logout()
                    } else {
                        val intent = Intent(this, LoginActivity::class.java)
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

        val menuButton = findViewById<ImageView>(R.id.menuButton_heartbeat)
        menuButton.setOnClickListener {
            // 메뉴 버튼을 클릭하면 Navigation Drawer를 열도록 함
            drawerLayout.openDrawer(GravityCompat.START)
        }
        updateWelcomeMessage()
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
            // 만약 Navigation Drawer가 열려 있다면, 닫기
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            // 그렇지 않으면 기본 동작 수행
            super.onBackPressed()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }
    private fun saveUserId(userId: String) {
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("userId", userId).apply()
    }

    private fun getUserId(): Int {
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("userId", 0)
    }
    private fun setupStepBarChart(barChart: BarChart) {
        // BarChart 설정
        barChart.setTouchEnabled(true)
        barChart.setPinchZoom(true)
        barChart.description = Description().apply { text = "" }
        barChart.setFitBars(true)
        barChart.animateY(1000)

        setupXAxisDate(stepBarChart)  // X축 날짜 설정

    }
    private fun setupCalorieBarChart(barChart: BarChart) {
        // BarChart 설정
        barChart.setTouchEnabled(true)
        barChart.setPinchZoom(true)
        barChart.description = Description().apply { text = "" }
        barChart.setFitBars(true)
        barChart.animateY(1000)

        setupXAxisDate(calorieBarChart)  // X축 날짜 설정

    }

    private fun setupXAxisDate(chart: BarChart) {

        chart.xAxis.setDrawLabels(true)
        chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        chart.xAxis.textSize = 12f
        chart.xAxis.textColor = Color.BLACK
        chart.xAxis.setDrawAxisLine(true)
        chart.xAxis.setDrawGridLines(false)
        chart.xAxis.granularity = 1f

        chart.invalidate()

    }
    private fun addDataToStepBarChart(barChart: BarChart, entries: List<BarEntry>) {
        // BarDataSet 생성
        val dataSet = BarDataSet(entries, "걸음 수")
        dataSet.color = Color.parseColor("#5271FE")  // 색상 설정 예제
        dataSet.valueTextColor = ContextCompat.getColor(this,R.color.black)
        dataSet.barBorderColor = Color.parseColor("#5271FE")

        // BarData 생성 및 설정
        val barData = BarData(dataSet)
        barData.setDrawValues(true)
        barData.setValueTextSize(10f)
        barData.barWidth = 0.16f

        // BarChart에 데이터 추가
        barChart.data = barData
//        barChart.setMaxVisibleValueCount(7)
        barChart.setFitBars(false)
        barChart.invalidate()
    }
    private fun addDataToCalorieBarChart(barChart: BarChart, entries: List<BarEntry>) {
        // BarDataSet 생성
        val dataSet = BarDataSet(entries, "총 소모 칼로리")
        dataSet.color = Color.parseColor("#5271FE")  // 색상 설정 예제
        dataSet.valueTextColor = ContextCompat.getColor(this,R.color.black)
        dataSet.barBorderColor = Color.parseColor("#5271FE")

        // BarData 생성 및 설정
        val barData = BarData(dataSet)
        barData.setDrawValues(true)
        barData.setValueTextSize(10f)
        barData.barWidth = 0.16f

        // BarChart에 데이터 추가
        barChart.data = barData
//        barChart.setMaxVisibleValueCount(7)
        barChart.setFitBars(false)
        barChart.invalidate()
    }

    private fun formatDate(dateString: String): String {
        // 원래의 날짜 형식인 "yyyy-MM-dd"로부터 Date 객체를 생성
        val originalFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date: Date = originalFormat.parse(dateString)

        // 새로운 형식 "dd/MM/yy"로 포맷 변경
        val newFormat = SimpleDateFormat("MM/dd", Locale.getDefault())
        return newFormat.format(date)
    }

    private fun parseJsonDataForStepCharts(jsonData: String) {
        try {
            val dataArray = JSONArray(jsonData)
            val entries = mutableListOf<BarEntry>()
            val dateLabels = mutableListOf<String>()

            for (i in 0 until dataArray.length()) {
                val item = dataArray.getJSONObject(i)
                val steps = item.getInt("steps").toFloat()
                val birthDate = item.getString("birthDate").substring(0,10)
                val formattedDate = formatDate(birthDate)
                dateLabels.add(formattedDate)
                entries.add(BarEntry(i.toFloat(), steps))
            }

            if (entries.isNotEmpty()) {
                stepBarChart.xAxis.valueFormatter = IndexAxisValueFormatter(dateLabels)

                addDataToStepBarChart(stepBarChart, entries)
                stepBarChart.invalidate()
            } else {
                showToast("No data found for chart.")
            }
        } catch (e: JSONException) {
            Log.e("SleepActivity", "Error parsing JSON data", e)
            showToast("Error parsing data for charts: ${e.localizedMessage}")
        }
    }

    private fun parseJsonDataForCalorieCharts(jsonData: String) {
        try {
            val dataArray = JSONArray(jsonData)
            val entries = mutableListOf<BarEntry>()
            val dateLabels = mutableListOf<String>()
            var totalCalories = 0f
            var count = 0

            for (i in 0 until dataArray.length()) {
                val item = dataArray.getJSONObject(i)
                val calTotal = item.getInt("calTotal").toFloat()
                totalCalories += calTotal
                count++

                val birthDate = item.getString("birthDate").substring(0,10)
                val formattedDate = formatDate(birthDate)
                dateLabels.add(formattedDate)
                entries.add(BarEntry(i.toFloat(), calTotal))
            }

            if (entries.isNotEmpty()) {
                calorieBarChart.xAxis.valueFormatter = IndexAxisValueFormatter(dateLabels)
                addDataToCalorieBarChart(calorieBarChart, entries)
                calorieBarChart.invalidate()

                // 평균 칼로리 계산 및 UI 업데이트
                val averageCalories = totalCalories / count
                updateCalorieUI(averageCalories)
            } else {
                showToast("No data found for chart.")
            }
        } catch (e: JSONException) {
            Log.e("CalorieActivity", "Error parsing JSON data", e)
            showToast("Error parsing data for charts: ${e.localizedMessage}")
        }
    }

    private fun post(apiURL: String, userId: Int): String {
        val client = OkHttpClient()

        val formBody = FormBody.Builder()
            .add("userId", userId.toString())
            .build()

        val request = Request.Builder()
            .url(apiURL)
            .post(formBody)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            return response.body?.string() ?: ""
        }
    }

    private fun fetchDataFromApi(url: String, userId: Int) {
        val client = OkHttpClient()
        val formBody = FormBody.Builder().add("userId", userId.toString()).build()

        val request = Request.Builder()
            .url(url)
            .post(formBody)
            .build()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    response.body?.string()?.let { jsonData ->
                        withContext(Dispatchers.Main) {
                            parseJsonDataForStepCharts(jsonData)
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        showToast("Failed to fetch data: ${response.message}")
                    }
                }
            } catch (e: IOException) {
                Log.e("CalorieActivity", "Network error: ${e.localizedMessage}")
                withContext(Dispatchers.Main) {
                    showToast("Network error: ${e.localizedMessage}")
                }
            }
        }
    }


    private fun fetchDataFromApiCalories(apiURL: String, userId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val jsonResponse = post(apiURL, userId)
                withContext(Dispatchers.Main) {
                    parseJsonDataForCalorieCharts(jsonResponse)
                }
            } catch (e: Exception) {
                Log.e("Activity", "Error fetching data", e)
            }
        }
    }
    private fun updateCalorieUI(averageCalories: Float) {
        // 평균 칼로리 표시
        calorie_text.text = "${String.format("%.1f", averageCalories)}kcal"

        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        // 평균 칼로리에 따라 아이콘 및 문구 변경
        when {
            averageCalories < 153.3f -> {
                calorie_icon.setImageResource(R.drawable.ic_bad)  // 낮은 칼로리 아이콘
                calorie_message.text = "칼로리 소모가 낮아요."
                editor.putString("calorieIcon", "ic_bad")
                editor.apply()
                editor.putString("calorieMessage", "칼로리 소모가 낮습니다.")
                editor.apply()
            }
            averageCalories in 153.4f..200f -> {
                calorie_icon.setImageResource(R.drawable.ic_good)  // 정상 칼로리 아이콘
                calorie_message.text = "칼로리 소모가 적당해요."
                editor.putString("calorieIcon", "ic_good")
                editor.apply()
                editor.putString("calorieMessage", "칼로리 소모가 적당합니다.")
                editor.apply()

            }
            else -> {
                calorie_icon.setImageResource(R.drawable.ic_good)  // 높은 칼로리 아이콘
                calorie_message.text = "칼로리 소모가 많아요."
                editor.putString("calorieIcon", "ic_good")
                editor.apply()
                editor.putString("calorieMessage", "칼로리 소모가 많습니다.")
                editor.apply()

            }
        }
    }



}
