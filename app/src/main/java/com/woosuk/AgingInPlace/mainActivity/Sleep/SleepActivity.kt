package com.woosuk.AgingInPlace.mainActivity.Sleep

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CalendarView
import android.widget.ImageView
import android.widget.TextView
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
import com.woosuk.AgingInPlace.mainActivity.MainActivity
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
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale


class SleepActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var lineChart: LineChart
    private lateinit var durationBarChart: BarChart
    private lateinit var remBarChart: BarChart
    private lateinit var averageSleepDurationText: TextView
    private lateinit var sleepDurationIcon: ImageView
    private lateinit var sleepDurationMessage: TextView
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var navView: NavigationView
    lateinit var healthConnectManager: HealthConnectManager

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        healthConnectManager = HealthConnectManager(this)
        setContentView(R.layout.activity_sleep)
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        // View 초기화
        averageSleepDurationText = findViewById(R.id.sleep_duration_text)
        sleepDurationIcon = findViewById(R.id.sleep_duration_icon)
        sleepDurationMessage = findViewById(R.id.sleep_duration_message)
        navView = findViewById(R.id.nav_view)

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

        val selectDateButton: Button = findViewById(R.id.select_date_button)
        selectDateButton.setOnClickListener {
            showDatePickerDialog()
        }

        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        drawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        // 헤더 뷰 접근
        val headerView = navView.getHeaderView(0)

        // 로그인 상태에 따라 헤더의 버튼 가시성 조정
        val menuLoginBtn: Button = headerView.findViewById(R.id.menu_loginBtn)
        menuLoginBtn.visibility = if (isLoggedIn) View.GONE else View.VISIBLE

        menuLoginBtn.setOnClickListener {
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
        val menuButton = findViewById<ImageView>(R.id.menuButton_heartbeat)
        menuButton.setOnClickListener {
            // 메뉴 버튼을 클릭하면 Navigation Drawer를 열도록 함
            drawerLayout.openDrawer(GravityCompat.START)
        }
        updateWelcomeMessage()
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
                updateDataForSelectedDate(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun updateDataForSelectedDate(date: LocalDate) {
        val startDateTime = LocalDateTime.of(date.minusDays(1), LocalTime.of(20, 0, 0)) // 전날 오후 8시
        val startTime = startDateTime.toInstant(ZoneOffset.ofHours(9)) // KST (UTC+9)

        val endDateTime = LocalDateTime.of(date, LocalTime.of(11, 0, 0)) // 당일 오전 11시
        val endTime = endDateTime.toInstant(ZoneOffset.ofHours(9)) // KST (UTC+9)

        CoroutineScope(Dispatchers.IO).launch {
            val sleepRecords = healthConnectManager.readSleep(startTime, endTime)

            withContext(Dispatchers.Main) {
                if (sleepRecords.isNotEmpty()) {
                    val firstRecord = sleepRecords.first()
                    val bedTimeStart = firstRecord.startTime
                    val bedTimeEnd = firstRecord.endTime

                    // 시간 포맷
                    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

                    // TextView에 시간 표시
                    val clock1TextView: TextView = findViewById(R.id.clock1_text)
                    val clock2TextView: TextView = findViewById(R.id.clock2_text)

                    clock1TextView.text = bedTimeStart.atZone(ZoneOffset.ofHours(9)).format(timeFormatter)
                    clock2TextView.text = bedTimeEnd.atZone(ZoneOffset.ofHours(9)).format(timeFormatter)
                } else {
                    val clock1TextView: TextView = findViewById(R.id.clock1_text)
                    val clock2TextView: TextView = findViewById(R.id.clock2_text)

                    clock1TextView.text = "N/A"
                    clock2TextView.text = "N/A"
                }
            }
        }
    }


    private fun updateWelcomeMessage() {
        // NavigationView의 헤더 가져오기
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
        editor.commit()

        // 로그인 화면으로 이동합니다.
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
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

    private fun setupDurationBarChart(barChart: BarChart) {
        // BarChart 설정
        barChart.setTouchEnabled(true)
        barChart.setPinchZoom(true)
        barChart.description = Description().apply { text = "" }
        barChart.animateY(1000)

        setupXAxisDate(barChart)  // X축 날짜 설정

    }
    private fun setupRemBarChart(barChart: BarChart) {
        // BarChart 설정
        barChart.setTouchEnabled(true)
        barChart.setPinchZoom(true)
        barChart.description = Description().apply { text = "" }
        barChart.animateY(1000)

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
        barData.barWidth = 0.16f

        // BarChart에 데이터 추가
        barChart.data = barData
//        barChart.setMaxVisibleValueCount(7)
        barChart.setFitBars(false)
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
        barData.barWidth = 0.16f
        // BarChart에 데이터 추가
        barChart.data = barData
//        barChart.setMaxVisibleValueCount(7)
        barChart.setFitBars(false)
        barChart.invalidate()
    }

    private fun parseJsonDataForDurationCharts(jsonData: String) {
        try {
            val dataArray = JSONArray(jsonData)
            val entries = mutableListOf<BarEntry>()
            val dateLabels = mutableListOf<String>()
            var totalSleep = 0f
            var count = 0

            for (i in 0 until dataArray.length()) {
                val item = dataArray.getJSONObject(i)
                val sleepTotal = item.getInt("duration").toFloat()
                totalSleep += sleepTotal
                count++

                val birthDate = item.getString("birthDate").substring(0, 10)
                val formattedDate = formatDate(birthDate)
                dateLabels.add(formattedDate)
                entries.add(BarEntry(i.toFloat(), sleepTotal))

            }

            if (entries.isNotEmpty()) {
                durationBarChart.xAxis.valueFormatter = IndexAxisValueFormatter(dateLabels)
                addDataToDurationBarChart(durationBarChart, entries)
                durationBarChart.invalidate()

                // 평균 수면 시간 계산 및 UI 업데이트
                val averageSleepDuration = totalSleep / count
                updateDurationUI(averageSleepDuration)

            } else {
                showToast("No data found for chart.")
            }
        } catch (e: JSONException) {
            Log.e("SleepActivity", "Error parsing JSON data", e)
            showToast("Error parsing data for charts: ${e.localizedMessage}")
        }
    }


    private fun updateDurationUI(averageSleepDuration: Float) {
        // 평균 수면 시간 표시
        averageSleepDurationText.text =  "${String.format("%.1f", averageSleepDuration)} 시간"

        // 평균 수면 시간에 따라 아이콘 및 문구 변경
        when {
            averageSleepDuration < 6 -> {
                sleepDurationIcon.setImageResource(R.drawable.ic_bad)  // 부족한 수면 시간 아이콘
                sleepDurationMessage.text = "수면시간이 부족합니다.."
            }
            averageSleepDuration in 6f..8f -> {
                sleepDurationIcon.setImageResource(R.drawable.ic_good)  // 정상 수면 시간 아이콘
                sleepDurationMessage.text = "좋은 수면습관 입니다.."
            }
            else -> {
                sleepDurationIcon.setImageResource(R.drawable.ic_bad)  // 과도한 수면 시간 아이콘
                sleepDurationMessage.text = "수면시간이 많습니다."
            }
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
                val formattedDate = formatDate(birthDate)
                dateLabels.add(formattedDate)
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
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("userId", 0)
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
    private fun formatDate(dateString: String): String {
        // 원래의 날짜 형식인 "yyyy-MM-dd"로부터 Date 객체를 생성
        val originalFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date: Date = originalFormat.parse(dateString)

        // 새로운 형식 "dd/MM/yy"로 포맷 변경
        val newFormat = SimpleDateFormat("MM/dd", Locale.getDefault())
        return newFormat.format(date)
    }


}

class CustomClockView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var hour = 0
    private var minute = 0
    private val paint = Paint()

    // 시간을 설정하는 메서드
    fun setTime(hour: Int, minute: Int) {
        this.hour = hour
        this.minute = minute
        invalidate() // View를 다시 그려줍니다.
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawClockFace(canvas)
        drawHands(canvas)
    }

    private fun drawClockFace(canvas: Canvas) {
        paint.style = Paint.Style.STROKE
        paint.color = Color.BLACK
        paint.strokeWidth = 8f

        // 시계판을 그립니다.
        val radius = Math.min(width, height) / 2 - 16
        canvas.drawCircle(width / 2f, height / 2f, radius.toFloat(), paint)
    }

    private fun drawHands(canvas: Canvas) {
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 8f

        val centerX = width / 2f
        val centerY = height / 2f
        val radius = Math.min(width, height) / 2 - 16

        // 시침을 그립니다.
        val hourAngle = Math.toRadians((hour * 30 + minute * 0.5).toDouble()).toFloat()
        val hourX = (centerX + radius * 0.5 * Math.cos(hourAngle.toDouble())).toFloat()
        val hourY = (centerY + radius * 0.5 * Math.sin(hourAngle.toDouble())).toFloat()
        canvas.drawLine(centerX, centerY, hourX, hourY, paint)

        // 분침을 그립니다.
        paint.strokeWidth = 6f
        val minuteAngle = Math.toRadians((minute * 6).toDouble()).toFloat()
        val minuteX = (centerX + radius * 0.75 * Math.cos(minuteAngle.toDouble())).toFloat()
        val minuteY = (centerY + radius * 0.75 * Math.sin(minuteAngle.toDouble())).toFloat()
        canvas.drawLine(centerX, centerY, minuteX, minuteY, paint)
    }
}

