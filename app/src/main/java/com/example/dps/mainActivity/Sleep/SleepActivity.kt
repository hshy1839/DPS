package com.example.dps.mainActivity.Sleep

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
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
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.text.util.LocalePreferences.FirstDayOfWeek.Days
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.lifecycle.ReportFragment.Companion.reportFragment
import androidx.lifecycle.lifecycleScope
import com.example.dps.HealthConnectManager
import com.example.dps.R
import com.example.dps.loginActivity.LoginActivity
import com.example.dps.restClient.models.ActivityDataVO
import com.example.dps.restClient.models.AvroRESTVO
import com.example.dps.restClient.models.SleepDataVO
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.Chart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.Random


class SleepActivity : AppCompatActivity() {

    private lateinit var healthConnectManager: HealthConnectManager
    private val PERMISSION_REQUEST_CODE = 1001
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var lineChart: LineChart
    private lateinit var durationBarChart: BarChart
    private lateinit var remBarChart: BarChart
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        healthConnectManager = HealthConnectManager(this)
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


        fetchDataFromDurationApi("http://43.200.2.115:8080/chart/duration", "ChartTest2")
        fetchDataFromRemApi("http://43.200.2.115:8080/chart/rem", "ChartTest2")

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
                val sleepTotal = item.getInt("sleep_data.sleep_duration").toFloat()
                val timestamp = item.getLong("sleep_data.create_date")
                val formattedDate = formatDate(timestamp)
                dateLabels.add(formattedDate)
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
                val rem = item.getInt("sleep_data.sleep_rem").toFloat()
                val timestamp = item.getLong("sleep_data.create_date")
                val formattedDate = formatDate(timestamp)
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

    private fun getUserId(): String? {
        val sharedPreferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("userId", null)
    }

    private fun post(apiURL: String, userId: String): String {
        val client = OkHttpClient()

        val formBody = FormBody.Builder()
            .add("username", userId)
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

    private fun fetchDataFromDurationApi(apiURL: String, userId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val jsonResponse = post(apiURL, userId)
                withContext(Dispatchers.Main) {
                    parseJsonDataForDurationCharts(jsonResponse)
                }
            } catch (e: Exception) {
                Log.e("SleepActivity", "Error fetching data", e)
            }
        }
    }
    private fun fetchDataFromRemApi(apiURL: String, userId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val jsonResponse = post(apiURL, userId)
                withContext(Dispatchers.Main) {
                    parseJsonDataForRemCharts(jsonResponse)
                }
            } catch (e: Exception) {
                Log.e("SleepActivity", "Error fetching data", e)
            }
        }
    }


    fun writeTrainCsv(@Suppress("UNUSED_PARAMETER") view: View) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
            return
        }
        CoroutineScope(Dispatchers.IO).launch  {
            if (!healthConnectManager.hasAllPermissions()) {
                showDialogInfo(R.string.permissions_not_granted)
                return@launch
            }


            val today = LocalDate.now(ZoneId.of("Asia/Seoul"))
            val yesterday =  today.minusMonths(3)

            val startDateTime = LocalDateTime.of(yesterday, LocalTime.of(0, 0, 1))
            val dayStart = startDateTime.toInstant(ZoneOffset.UTC)

            val endDateTime = LocalDateTime.of(today, LocalTime.of(23, 59, 59))
            val dayEnd = endDateTime.toInstant(ZoneOffset.UTC)


            val calActive = healthConnectManager.readCalActive(
                dayStart,
                dayEnd
            )

            val calTotal = healthConnectManager.readCalTotalRecords(
                dayStart,
                dayEnd
            )

            val dailyMovement = healthConnectManager.readTotalDistance(
                dayStart,
                dayEnd
            )

            val distance = healthConnectManager.readDistance(
                dayStart,
                dayEnd
            )

            val steps = healthConnectManager.readTotalSteps(
                dayStart,
                dayEnd
            )

            val exerciseTime = healthConnectManager.readExerciseTime(
                dayStart,
                dayEnd
            )
            val bmr = healthConnectManager.readBMR(
                dayStart,
                dayEnd
            )

            val cal = calTotal.substring(0 until 4 )
            val Bmr = bmr.substring(0 until 4)
            val activeCalrorie = cal.toInt()-Bmr.toInt()

            val calTotalInt = cal.toInt()


            // 활동 데이터 인스턴스 생성
//            val data = ActivityDataVO(
//                "Lee", "운동_이석영", Instant.now(), Instant.now(),activeCalrorie,
//                calTotalInt, dailyMovement.substring(0 until 3).toInt(), dayEnd, dayStart, 0, 0, 0, 10,
//                0, 0, 100, 100, steps!!.toInt(), exerciseTime!!.toInt(), false
//            )
                //데이터 전송
//            Avropost(data, "http://3.34.218.215:8082/topics/activity_data/")

            Log.i("ddd", "하루간 활동 칼로리: ${calActive}")

            Log.i("ddd", "하루간 총 사용 칼로리: ${calTotalInt}")

            Log.i("ddd", "매일 움직인 거리: ${dailyMovement}")

            Log.i("ddd", "활동 종료 시간: ${dayEnd}")

            Log.i("ddd", "활동 시작 시간: ${dayStart}")

            Log.i("ddd", "매일 걸음 수: ${steps}")

            Log.i("ddd", "활동 총 시간: ${exerciseTime}")

            Log.i("ddd", "BMR: ${bmr}")

        }
    }

fun writeSleep(@Suppress("UNUSED_PARAMETER") view: View) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
            return
        }

        lifecycleScope.launch {
            if (!healthConnectManager.hasAllPermissions()) {
                showDialogInfo(R.string.permissions_not_granted)
                return@launch

            }

//            val today = LocalDate.now(ZoneId.of("Asia/Seoul"))
//            val yesterday = today.minusDays(1)

//            val startDateTime = LocalDateTime.of(yesterday, LocalTime.of(0, 0, 1))
//            val startTime = startDateTime.toInstant(ZoneOffset.UTC)
//            val endDateTime = LocalDateTime.of(today, LocalTime.of(23, 59, 59))
//            val endTime = endDateTime.toInstant(ZoneOffset.UTC)

            val twoMonthsAgo = LocalDate.now(ZoneId.of("Asia/Seoul")).minusMonths(1)

            // 두 달 전 자정부터 시작합니다.
            val startDateTime = LocalDateTime.of(twoMonthsAgo, LocalTime.of(0, 0, 1))
            val startTime = startDateTime.toInstant(ZoneOffset.UTC)

            // 현재 날짜의 23시 59분 59초까지를 종료 시간으로 설정합니다.
            val today = LocalDate.now(ZoneId.of("Asia/Seoul"))
            val endDateTime = LocalDateTime.of(today, LocalTime.of(23, 59, 59))
            val endTime = endDateTime.toInstant(ZoneOffset.UTC)


            val sleep = healthConnectManager.readSleep(
                startTime,
                endTime
            )

            var awake = 0
            var bedTimeEnd: Instant = Instant.now() // 현재 시간으로 초기화
            var bedTimeStart: Instant = Instant.now() // 현재 시간으로 초기화
            var breathAverage = 0.0
            var deep = 0
            var stageDuration = 0
            var HRAverage = 0.0
            var HRLowest = 0.0
            var light = 0
            var rem = 0
            var durationMinutes = 0



            for (sleepRecord in sleep) {

                val rate = healthConnectManager.readRespiratoryRateRecords(
                    sleepRecord.startTime,
                    sleepRecord.endTime
                )

                val heartRate = healthConnectManager.readHeartRateAggregate(
                    sleepRecord.startTime,
                    sleepRecord.endTime,
                )
                HRAverage = heartRate.first
                HRLowest = heartRate.second

                for(rates in rate){
                    breathAverage = rates.rate
                }
                for (stage in sleepRecord.stages) {

                    bedTimeStart = sleepRecord.startTime.atZone(ZoneId.of("Asia/Seoul")).toInstant()
                    bedTimeEnd =sleepRecord.endTime.atZone(ZoneId.of("Asia/Seoul")).toInstant()

                    durationMinutes = Duration.between(bedTimeStart, bedTimeEnd).toMinutes().toInt()

                    val stageStart = stage.startTime.atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime()
                    val stageEnd = stage.endTime.atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime()
                    stageDuration = Duration.between(stageStart, stageEnd).toMinutes().toInt()


                    // Awake 시간인 경우
                    if (stage.stage == SleepSessionRecord.STAGE_TYPE_AWAKE || stage.stage == SleepSessionRecord.STAGE_TYPE_AWAKE_IN_BED) {
                        awake += stageDuration
                    }

                    // Deep Sleep 시간인 경우
                    if (stage.stage == SleepSessionRecord.STAGE_TYPE_DEEP) {
                        deep += stageDuration
                    }
                    // Rem Sleep 시간인 경우
                    if (stage.stage == SleepSessionRecord.STAGE_TYPE_REM) {
                        rem += stageDuration

                    }
                    // Light Sleep 시간인 경우
                    if (stage.stage == SleepSessionRecord.STAGE_TYPE_LIGHT) {
                        light += stageDuration

                    }

                }
                val epochSeconds = 1715455805L
                val instant = Instant.ofEpochSecond(epochSeconds)

                    val data: AvroRESTVO = SleepDataVO(
                        "ChartTest2", "이석영12345", Instant.now(),
                        Instant.now(),500,
                        bedTimeEnd, bedTimeStart, breathAverage, deep, durationMinutes, HRAverage, HRLowest, light,
                        rem, durationMinutes+stageDuration, true)
                    Avropost(data, "http://3.34.218.215:8082/topics/sleep_data/")

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
    fun Avropost(data: AvroRESTVO, apiURL: String?) {
        CoroutineScope(Dispatchers.IO).launch {
            var output: OutputStream? = null
            var reader: BufferedReader? = null
            var writer: BufferedWriter? = null
            var conn: HttpURLConnection? = null
            val connTimeout = 5000
            val readTimeout = 3000
            try {
                val url = URL(apiURL)
                conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.connectTimeout = connTimeout
                conn.readTimeout = readTimeout
                conn.setRequestProperty("Content-Type", "application/vnd.kafka.avro.v2+json")
                conn.doOutput = true
                conn.instanceFollowRedirects = true
                output = conn.outputStream
                writer = BufferedWriter(OutputStreamWriter(output))
                writer.write(data.toRESTMessage())
                writer.flush()
                val buffer = StringBuilder()
                if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                    reader = BufferedReader(InputStreamReader(conn.inputStream, "UTF-8"))
                    var message: String? = null
                    while (reader.readLine().also { message = it } != null) {
                        buffer.append(message).append("\n")
                    }
                } else {
                    buffer.append("code : ")
                    buffer.append(conn.responseCode).append("\n")
                    buffer.append("message : ")
                    buffer.append(conn.responseMessage).append("\n")
                }
                println(buffer.toString())
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                try {
                    writer?.close()
                    output?.close()
                    reader?.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}


