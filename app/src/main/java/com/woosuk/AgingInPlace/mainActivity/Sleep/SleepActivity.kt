package com.woosuk.AgingInPlace.mainActivity.Sleep

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.woosuk.AgingInPlace.HealthConnectManager
import com.woosuk.AgingInPlace.R
import com.woosuk.AgingInPlace.loginActivity.LoginActivity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.material.navigation.NavigationView
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


class SleepActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var lineChart: LineChart
    private lateinit var durationBarChart: BarChart
    private lateinit var remBarChart: BarChart
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_sleep)

        val loginButton = findViewById<ImageView>(R.id.loginButton)
        loginButton.setOnClickListener {
            val intent = Intent(this@SleepActivity, LoginActivity::class.java)
            startActivity(intent)
        }

        durationBarChart = findViewById<BarChart>(R.id.durationBarChart)
        setupDurationBarChart(durationBarChart)

        remBarChart = findViewById<BarChart>(R.id.remBarChart)
        setupRemBarChart(remBarChart)

        var id = getUserId()
        fetchDataFromDurationApi("http://3.39.236.95:8080/chart/duration", id)
        fetchDataFromRemApi("http://3.39.236.95:8080/chart/rem", id)

        val backArrow = findViewById<ImageView>(R.id.back_arrow)
        backArrow.setOnClickListener {
            onBackPressed()
        }



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
        // 뒤로가기 버튼을 누를 때
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
    private fun setupDurationBarChart(barChart: BarChart) {
        // BarChart 설정
        barChart.setTouchEnabled(true)
        barChart.setPinchZoom(true)
        barChart.description = Description().apply { text = "" }
        barChart.animateX(500)

        setupXAxisDate(barChart)  // X축 날짜 설정

    }
    private fun setupRemBarChart(barChart: BarChart) {
        // BarChart 설정
        barChart.setTouchEnabled(true)
        barChart.setPinchZoom(true)
        barChart.description = Description().apply { text = "" }
        barChart.animateX(500)

        setupXAxisDate(barChart)  // X축 날짜 설정

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
    private fun addDataToDurationBarChart(barChart: BarChart, entries: List<BarEntry>) {
        // BarDataSet 생성
        val dataSet = BarDataSet(entries, "수면 시간")
        dataSet.color = Color.parseColor("#5271FE")  // 색상 설정 예제
        dataSet.valueTextColor = ContextCompat.getColor(this,R.color.black)
        dataSet.barBorderColor = Color.parseColor("#5271FE")

        // BarData 생성 및 설정
        val barData = BarData(dataSet)
        barData.setDrawValues(true)
        barData.setValueTextSize(10f)


        // BarChart에 데이터 추가
        barChart.data = barData
//        barChart.setMaxVisibleValueCount(7)
        barChart.setFitBars(true)
        barChart.invalidate()
    }
    private fun addDataToRemBarChart(barChart: BarChart, entries: List<BarEntry>) {
        // BarDataSet 생성
        val dataSet = BarDataSet(entries, "수면 시간")
        dataSet.color = Color.parseColor("#5271FE")  // 색상 설정 예제
        dataSet.valueTextColor = ContextCompat.getColor(this,R.color.black)
        dataSet.barBorderColor = Color.parseColor("#5271FE")

        // BarData 생성 및 설정
        val barData = BarData(dataSet)
        barData.setDrawValues(true)
        barData.setValueTextSize(10f)

        // BarChart에 데이터 추가
        barChart.data = barData
//        barChart.setMaxVisibleValueCount(7)
        barChart.setFitBars(true)
        barChart.invalidate()
    }
    private fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("MM-dd", Locale.getDefault())
        return sdf.format(Date(timestamp * 1000)) // timestamp는 초 단위로 가정
    }

    private fun parseJsonDataForDurationCharts(jsonData: String) {
        try {
            val dataArray = JSONArray(jsonData)
            val entries = mutableListOf<BarEntry>()
            val dateLabels = mutableListOf<String>()

            for (i in 0 until dataArray.length()) {
                val item = dataArray.getJSONObject(i)
                val sleepTotal = item.getInt("duration").toFloat()
                val birthDate = item.getString("birthDate").substring(0,10)
                //val formattedDate = formatDate(timestamp)
                dateLabels.add(birthDate)
                entries.add(BarEntry(i.toFloat(), sleepTotal))
            }

            if (entries.isNotEmpty()) {
                durationBarChart.xAxis.valueFormatter = IndexAxisValueFormatter(dateLabels)

                addDataToDurationBarChart(durationBarChart, entries)
                durationBarChart.invalidate()
            } else {
                showToast("No data found for chart.")
            }
        } catch (e: JSONException) {
            Log.e("SleepActivity", "Error parsing JSON data", e)
            showToast("Error parsing data for charts: ${e.localizedMessage}")
        }
    }
    private fun parseJsonDataForRemCharts(jsonData: String) {
        try {
            val dataArray = JSONArray(jsonData)
            val entries = mutableListOf<BarEntry>()
            val dateLabels = mutableListOf<String>()

            for (i in 0 until dataArray.length()) {
                val item = dataArray.getJSONObject(i)
                val rem = item.getInt("rem").toFloat()
                val birthDate = item.getString("birthDate").substring(0,10)
               // val formattedDate = formatDate(timestamp)
                dateLabels.add(birthDate)
                entries.add(BarEntry(i.toFloat(), rem))
            }

            if (entries.isNotEmpty()) {
                remBarChart.xAxis.valueFormatter = IndexAxisValueFormatter(dateLabels)

                addDataToRemBarChart(remBarChart, entries)
                remBarChart.invalidate()
            } else {
                showToast("No data found for chart.")
            }
        } catch (e: JSONException) {
            Log.e("SleepActivity", "Error parsing JSON data", e)
            showToast("Error parsing data for charts: ${e.localizedMessage}")
        }
    }

    private fun saveUserId(userId: String) {
        val sharedPreferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("userId", userId).apply()
    }

    private fun getUserId(): Int {
        val sharedPreferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("userId", 120)
    }

    private fun post(apiURL: String, userId: String): String {
        val client = OkHttpClient()

        val formBody = FormBody.Builder()
            .add("userId", userId)
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

    private fun fetchDataFromDurationApi(apiURL: String, userId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val jsonResponse = post(apiURL, userId.toString())
                withContext(Dispatchers.Main) {
                    parseJsonDataForDurationCharts(jsonResponse)
                }
            } catch (e: Exception) {
                Log.e("SleepActivity", "Error fetching data", e)
            }
        }
    }
    private fun fetchDataFromRemApi(apiURL: String, userId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val jsonResponse = post(apiURL, userId.toString())
                withContext(Dispatchers.Main) {
                    parseJsonDataForRemCharts(jsonResponse)
                }
            } catch (e: Exception) {
                Log.e("SleepActivity", "Error fetching data", e)
            }
        }
    }

    private fun showDialogInfo(resId: Int) {
        runOnUiThread {
            AlertDialog.Builder(this)
                .setMessage(resId)
                .setPositiveButton("Ok", null)
                .show()
        }
    }

    fun returnToMenu(@Suppress("UNUSED_PARAMETER") view: View) {
        finish()
    }

}


