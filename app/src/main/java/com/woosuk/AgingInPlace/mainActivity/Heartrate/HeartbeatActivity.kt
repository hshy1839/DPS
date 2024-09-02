package com.woosuk.AgingInPlace.mainActivity.Heartrate

import android.annotation.SuppressLint
import android.content.Context
import com.woosuk.AgingInPlace.loginActivity.LoginActivity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
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

class HeartbeatActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var heartRateLineChart: LineChart
    private lateinit var heartRateBarChart: BarChart
    private lateinit var averageHeartRateText: TextView
    private lateinit var heartRateIcon: ImageView
    private lateinit var heartRateMessage: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_heartbeat)

        // View 초기화
        averageHeartRateText = findViewById(R.id.average_heart_rate_text)
        heartRateIcon = findViewById(R.id.heart_rate_icon)
        heartRateMessage = findViewById(R.id.heart_rate_message)

        // 기존 코드
        heartRateLineChart = findViewById(R.id.HeartRatelineChart)
        setupHeartRateChart(heartRateLineChart)

        val loginButton = findViewById<ImageView>(R.id.loginButton)
        loginButton.setOnClickListener {
            val intent = Intent(this@HeartbeatActivity, LoginActivity::class.java)
            startActivity(intent)
        }

        val backArrow = findViewById<ImageView>(R.id.back_arrow)
        backArrow.setOnClickListener {
            onBackPressed()
        }

        heartRateLineChart = findViewById(R.id.HeartRatelineChart)
        setupHeartRateChart(heartRateLineChart)

        var id = getUserId()
        fetchDataFromApi("http://3.39.236.95:8080/chart/heartRate", id)



        drawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        // 토글 버튼을 추가하여 메뉴가 열리고 닫히도록 함
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // 네비게이션 메뉴 아이템 클릭 리스너 설정
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_item1 -> {
                    // Menu 1 선택 시의 동작
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.aginginplaces.net/"))
                    startActivity(intent)
                }
                R.id.nav_item2 -> {
                    // Menu 2 선택 시의 동작
                    showToast("고객센터 이동 버튼")
                }
                R.id.nav_item3 -> {
                    // Menu 3 선택 시의 동작
                    showToast("설정 버튼")
                }
                R.id.nav_item4 -> {
                    // Menu 3 선택 시의 동작
                    showToast("로그아웃 버튼")
                }
            }
            // 메뉴를 선택한 후에는 Drawer를 닫아줌
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        val menuButton = findViewById<ImageView>(R.id.menuButton_heartbeat)
        menuButton.setOnClickListener {
            // 메뉴 버튼을 클릭하면 Navigation Drawer를 열도록 함
            drawerLayout.openDrawer(GravityCompat.START)
        }
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
        val sharedPreferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("userId", userId).apply()
    }

    private fun getUserId(): Int {
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("userId", 0)
    }
    private fun setupHeartRateChart(lineChart: LineChart) {
        // BarChart 설정
        lineChart.setTouchEnabled(true)
        lineChart.setPinchZoom(true)
        lineChart.description = Description().apply { text = "" }
        lineChart.animateY(1000)

        setupXAxisDate(heartRateLineChart)  // X축 날짜 설정

    }
   /* private fun setupCalorieBarChart(barChart: BarChart) {
        // BarChart 설정
        barChart.setTouchEnabled(true)
        barChart.setPinchZoom(true)
        barChart.description = Description().apply { text = "" }
        barChart.setFitBars(true)
        barChart.animateX(1000)

        setupXAxisDate(calorieBarChart)  // X축 날짜 설정

    }*/

    private fun setupXAxisDate(chart: LineChart) {

        chart.xAxis.setDrawLabels(true)
        chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        chart.xAxis.textSize = 12f
        chart.xAxis.textColor = Color.BLACK
        chart.xAxis.setDrawAxisLine(true)
        chart.xAxis.setDrawGridLines(false)
        chart.xAxis.granularity = 1f
        chart.xAxis.spaceMin = 0.2f  // 첫 번째 데이터 포인트의 왼쪽 여백 추가
        chart.xAxis.spaceMax = 0.2f  // 마지막 데이터 포인트의 오른쪽 여백 추가

        chart.invalidate()

    }
    private fun addDataToHeartRateChart(lineChart: LineChart, entries: List<Entry>) {
        // BarDataSet 생성
        val dataSet = LineDataSet(entries, "심박수")
        dataSet.color = Color.parseColor("#5271FE")  // 색상 설정 예제
        dataSet.valueTextColor = ContextCompat.getColor(this,R.color.black)
        dataSet.color = Color.parseColor("#5271FE")

        // BarData 생성 및 설정
        val barData = LineData(dataSet)
        barData.setDrawValues(true)
        barData.setValueTextSize(10f)

        // BarChart에 데이터 추가
        lineChart.data = barData
        lineChart.invalidate()
    }

    private fun formatDate(dateString: String): String {
        // 원래의 날짜 형식인 "yyyy-MM-dd"로부터 Date 객체를 생성
        val originalFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date: Date = originalFormat.parse(dateString)

        // 새로운 형식 "dd/MM/yy"로 포맷 변경
        val newFormat = SimpleDateFormat("MM/dd", Locale.getDefault())
        return newFormat.format(date)
    }

    private fun parseJsonDataForHeartRateCharts(jsonData: String) {
        try {
            val dataArray = JSONArray(jsonData)
            val entries = mutableListOf<Entry>()
            val dateLabels = mutableListOf<String>()
            var totalHeartRate = 0f
            var count = 0

            for (i in 0 until dataArray.length()) {
                val item = dataArray.getJSONObject(i)
                val heartRate = item.getInt("HRAverage").toFloat()
                totalHeartRate += heartRate
                count++

                val birthDate = item.getString("birthDate").substring(0, 10)
                val formattedDate = formatDate(birthDate)
                dateLabels.add(formattedDate)
                entries.add(Entry(i.toFloat(), heartRate))
            }

            if (entries.isNotEmpty()) {
                heartRateLineChart.xAxis.valueFormatter = IndexAxisValueFormatter(dateLabels)
                addDataToHeartRateChart(heartRateLineChart, entries)
                heartRateLineChart.invalidate()

                // 평균 심박수 계산 및 UI 업데이트
                val averageHeartRate = totalHeartRate / count
                updateHeartRateUI(averageHeartRate)
            } else {
                showToast("No data found for chart.")
            }
        } catch (e: JSONException) {
            Log.e("HeartRateActivity", "Error parsing JSON data", e)
            showToast("Error parsing data for charts: ${e.localizedMessage}")
        }
    }
   /* private fun parseJsonDataForCalorieCharts(jsonData: String) {
        try {
            val dataArray = JSONArray(jsonData)
            val entries = mutableListOf<BarEntry>()
            val dateLabels = mutableListOf<String>()

            for (i in 0 until dataArray.length()) {
                val item = dataArray.getJSONObject(i)
                val HRAverage = item.getDouble("HRAverage").toFloat()
                val birthDate = item.getString("birthDate")
                // val formattedDate = formatDate(timestamp)
                dateLabels.add(birthDate)
                entries.add(BarEntry(i.toFloat(), HRAverage))
            }

            if (entries.isNotEmpty()) {
                heartRateLineChart.xAxis.valueFormatter = IndexAxisValueFormatter(dateLabels)

                addDataToHeartRateChart(heartRateLineChart, entries)
                heartRateLineChart.invalidate()
            } else {
                showToast("No data found for chart.")
            }
        } catch (e: JSONException) {
            Log.e("HeartRateActivity", "Error parsing JSON data", e)
            showToast("Error parsing data for charts: ${e.localizedMessage}")
        }
    }*/


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

    private fun fetchDataFromApi(apiURL: String, userId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val jsonResponse = post(apiURL, userId)
                withContext(Dispatchers.Main) {
                    parseJsonDataForHeartRateCharts(jsonResponse)
                }
            } catch (e: Exception) {
                Log.e("HeartRateActivity", "Error fetching data", e)
            }
        }
    }
  /*  private fun fetchDataFromApiCalories(apiURL: String, userId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val jsonResponse = post(apiURL, userId)
                withContext(Dispatchers.Main) {
                    parseJsonDataForCalorieCharts(jsonResponse)
                }
            } catch (e: Exception) {
                Log.e("HeartRateActivity", "Error fetching data", e)
            }
        }
    }*/
  private fun updateHeartRateUI(averageHeartRate: Float) {
      // 평균 심박수 표시
      averageHeartRateText.text = "$averageHeartRate bpm"

      // 평균 심박수에 따라 아이콘 및 문구 변경
      when {
          averageHeartRate < 60 -> {
              heartRateIcon.setImageResource(R.drawable.ic_bad)  // 낮은 심박수 아이콘
              heartRateMessage.text = "평균 심박수가 낮아요."
          }
          averageHeartRate in 60f..100f -> {
              heartRateIcon.setImageResource(R.drawable.ic_good)  // 정상 심박수 아이콘
              heartRateMessage.text = "평균 심박수가 적당해요."
          }
          else -> {
              heartRateIcon.setImageResource(R.drawable.ic_bad)  // 높은 심박수 아이콘
              heartRateMessage.text = "평균 심박수가 높아요."
          }
      }
  }


}

