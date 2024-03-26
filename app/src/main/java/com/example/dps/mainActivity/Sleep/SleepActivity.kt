package com.example.dps.mainActivity.Sleep

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.lifecycle.lifecycleScope
import com.example.dps.HealthConnectManager
import com.example.dps.loginActivity.LoginActivity
import com.example.dps.R
import com.github.mikephil.charting.charts.BarChart
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
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

class SleepActivity : AppCompatActivity() {

    private lateinit var healthConnectManager: HealthConnectManager
    private val PERMISSION_REQUEST_CODE = 1001
    private lateinit var drawerLayout: DrawerLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        healthConnectManager = HealthConnectManager(this)
        setContentView(R.layout.activity_sleep)

        val loginButton = findViewById<ImageView>(R.id.loginButton)
        loginButton.setOnClickListener {
            val intent = Intent(this@SleepActivity, LoginActivity::class.java)
            startActivity(intent)
        }

        val lineChart = findViewById<LineChart>(R.id.lineChart)
        setupLineChart(lineChart)

        val backArrow = findViewById<ImageView>(R.id.back_arrow)
        backArrow.setOnClickListener {
            onBackPressed()
        }

        // 시간별 심박수 데이터 추가 (예시)
        val lineEntries = mutableListOf<Entry>()
        lineEntries.add(Entry(0f, 80f))
        lineEntries.add(Entry(1f, 85f))
        lineEntries.add(Entry(2f, 90f))
        lineEntries.add(Entry(3f, 88f))
        lineEntries.add(Entry(4f, 95f))
        lineEntries.add(Entry(5f, 85f))
        lineEntries.add(Entry(6f, 75f))
        addDataToLineChart(lineChart, lineEntries)

        val barChart = findViewById<BarChart>(R.id.barChart)
        setupBarChart(barChart)

        // BarChart에 데이터 추가
        val barEntries = mutableListOf<BarEntry>()
        barEntries.add(BarEntry(0f, 150f))
        barEntries.add(BarEntry(1f, 170f))
        barEntries.add(BarEntry(2f, 200f))
        barEntries.add(BarEntry(3f, 180f))
        barEntries.add(BarEntry(4f, 190f))
        barEntries.add(BarEntry(5f, 130f))
        barEntries.add(BarEntry(6f, 140f))
        barEntries.add(BarEntry(7f, 160f))
        barEntries.add(BarEntry(8f, 130f))
        barEntries.add(BarEntry(9f, 190f))
        barEntries.add(BarEntry(10f, 170f))
        barEntries.add(BarEntry(11f, 180f))
        addDataToBarChart(barChart, barEntries)

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
    private fun setupLineChart(lineChart: LineChart) {
        // LineChart 설정
        lineChart.setTouchEnabled(true)
        lineChart.setPinchZoom(true)
        lineChart.description = Description().apply { text = "" }

        // X 축 설정
        val xAxis = lineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.valueFormatter = IndexAxisValueFormatter(arrayOf("일", "월", "화", "수", "목", "금", "토"))
        xAxis.setGranularity(1f) // X축 간격 설정

        // Y 축 설정
        val leftAxis = lineChart.axisLeft
        leftAxis.setDrawGridLines(false)
        leftAxis.setDrawZeroLine(false)

        val rightAxis = lineChart.axisRight
        rightAxis.isEnabled = false
    }

    private fun setupBarChart(barChart: BarChart) {
        // BarChart 설정
        barChart.setTouchEnabled(true)
        barChart.setPinchZoom(true)
        barChart.description = Description().apply { text = "" }

        // X 축 설정
        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.valueFormatter = IndexAxisValueFormatter(arrayOf("1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월"))
        xAxis.setGranularity(1f) // X축 간격 설정

        // Y 축 설정
        val leftAxis = barChart.axisLeft
        leftAxis.setDrawGridLines(false)
        leftAxis.setDrawZeroLine(false)

        val rightAxis = barChart.axisRight
        rightAxis.isEnabled = false
    }



    private fun addDataToLineChart(lineChart: LineChart, entries: List<Entry>) {
        // LineDataSet 생성
        val dataSet = LineDataSet(entries, "일별 수면량")
        dataSet.color = ContextCompat.getColor(this, R.color.black)
        dataSet.valueTextColor = ContextCompat.getColor(this, R.color.black)

        // LineData 생성 및 설정
        val lineData = LineData(dataSet)
        lineData.setDrawValues(true)

        // LineChart에 데이터 추가
        lineChart.data = lineData
        lineChart.invalidate()
    }

    private fun addDataToBarChart(barChart: BarChart, entries: List<BarEntry>) {
        // BarDataSet 생성
        val dataSet = BarDataSet(entries, "월별 수면량")
        dataSet.color = Color.parseColor("#5271FE")
        dataSet.valueTextColor = ContextCompat.getColor(this, R.color.black)

        // BarData 생성 및 설정
        val barData = BarData(dataSet)
        barData.setDrawValues(true)

        // BarChart에 데이터 추가
        barChart.data = barData
        barChart.invalidate()
    }

    // 외부 저장소에 있는 Download 폴더의 경로 반환
    fun getTrainDownloadFilePath(): String {
        val folder = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        return File(folder, "Train.csv").absolutePath
    }
    fun getSleepDownloadFilePath(): String {
        val folder = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        return File(folder, "Sleep.csv").absolutePath
    }

    // CSV 데이터를 파일로 저장하는 함수
    fun appendToCSV(csvData: List<String>, filePath: String) {
        val file = File(filePath)
        try {
            val writer = FileWriter(file, true) // true를 넘겨서 파일을 추가 모드로 열기

            for (line in csvData) {
                writer.append(line)
                writer.append('\n')
            }

            writer.flush()
            writer.close()

            Log.i("CSV", "데이터가 CSV 파일에 추가되었습니다. 경로: $filePath")
        } catch (e: IOException) {
            Log.e("CSV", "CSV 파일에 데이터 추가 중 오류 발생: ${e.message}")
        }
    }


    fun writeTrainCsv(@Suppress("UNUSED_PARAMETER") view: View) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
            return
        }

        lifecycleScope.launch {
            if (!healthConnectManager.hasAllPermissions()) {
                showDialogInfo(R.string.permissions_not_granted)
                return@launch
            }

            val today = LocalDate.now(ZoneId.of("Asia/Seoul"))
            val yesterday = today.minusDays(4)

            val startDateTime = LocalDateTime.of(yesterday, LocalTime.of(0, 0, 1))
            val startTime = startDateTime.atZone(ZoneId.of("Asia/Seoul")).toInstant()
            val endTime = today.atTime(23, 59, 59).atZone(ZoneId.of("Asia/Seoul")).toInstant()


            val calActiveAggregate = healthConnectManager.readCalActiveRecords(
                startTime,
                endTime
            )
            val calActive = healthConnectManager.readActive(
                startTime,
                endTime
            )

            val calTotal = healthConnectManager.readCalTotalRecords(
                startTime,
                endTime
            )

            val distanceAggregate = healthConnectManager.readTotalDistance(
                startTime,
                endTime
            )

            val distance = healthConnectManager.readDistance(
                startTime,
                endTime
            )

            val steps = healthConnectManager.readTotalSteps(
                startTime,
                endTime
            )

            val exerciseTime = healthConnectManager.readExerciseTime(
                startTime,
                endTime
            )

            val csvData = mutableListOf<String>()

            var activeCalorie = 0.0

            for ( active in calActive){
                activeCalorie = active.energy.inCalories
            }

            var distance_A = 0.0

            for ( distance in distance){
                distance_A = distance.distance.inMeters
            }



            Log.i("ddd", "하루간 활동 칼로리: ${activeCalorie}")

            Log.i("ddd", "하루간 총 사용 칼로리: ${calTotal}")

            Log.i("ddd", "매일 움직인 거리: ${distance_A}")

            Log.i("ddd", "활동 종료 시간: ${endTime}")

            Log.i("ddd", "활동 시작 시간: ${startTime}")

            Log.i("ddd", "매일 걸음 수: ${steps}")

            Log.i("ddd", "활동 총 시간: ${exerciseTime}")


            csvData.add("${activeCalorie},${calTotal},${distance},${endTime},${startTime},${steps},${exerciseTime}")

            appendToCSV(csvData, getTrainDownloadFilePath())
        }
    }


    fun writeSleepCsv(@Suppress("UNUSED_PARAMETER") view: View) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
            return
        }

        lifecycleScope.launch {
            if (!healthConnectManager.hasAllPermissions()) {
                showDialogInfo(R.string.permissions_not_granted)
                return@launch

            }
            val csvData = mutableListOf<String>()

            val today = LocalDate.now(ZoneId.of("Asia/Seoul"))
            val yesterday = today.minusDays(10)

            val startDateTime = LocalDateTime.of(yesterday, LocalTime.of(0, 0, 1))
            val startTime = startDateTime.atZone(ZoneId.of("Asia/Seoul")).toInstant()
            val endTime = today.atTime(23, 59, 59).atZone(ZoneId.of("Asia/Seoul")).toInstant()


            val sleep = healthConnectManager.readSleep(
                startTime,
                endTime
            )

            var totalAwakeDuration = 0L
            var totalDeepSleepDuration = 0L
            var totalRemSleepDuration = 0L
            var totalLightSleepDuration = 0L
            var sleepStart: LocalDateTime = LocalDateTime.now() // 현재 시간으로 초기화
            var sleepEnd: LocalDateTime = LocalDateTime.now() // 현재 시간으로 초기화
            var durationMinutes = 0L
            var stageDuration = 0L

            var ratesRate = 0.0


            for (sleepRecord in sleep) {

                val rate = healthConnectManager.readRespiratoryRateRecords(
                    sleepRecord.startTime,
                    sleepRecord.endTime
                )

                val heartRate = healthConnectManager.readHeartRateAggregate(
                    sleepRecord.startTime,
                    sleepRecord.endTime,
                )

                for(rates in rate){
                    ratesRate = rates.rate
                }
                for (stage in sleepRecord.stages) {

                    sleepStart = sleepRecord.startTime.atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime()
                    sleepEnd =sleepRecord.endTime.atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime()

                    durationMinutes = Duration.between(sleepStart, sleepEnd).toMinutes()

                    val stageStart = stage.startTime.atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime()
                    val stageEnd = stage.endTime.atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime()
                    stageDuration = Duration.between(stageStart, stageEnd).toMinutes()


                    // Awake 시간인 경우
                    if (stage.stage == SleepSessionRecord.STAGE_TYPE_AWAKE || stage.stage == SleepSessionRecord.STAGE_TYPE_AWAKE_IN_BED) {
                        totalAwakeDuration += stageDuration
                    }

                    // Deep Sleep 시간인 경우
                    if (stage.stage == SleepSessionRecord.STAGE_TYPE_DEEP) {
                        totalDeepSleepDuration += stageDuration
                    }
                    // Rem Sleep 시간인 경우
                    if (stage.stage == SleepSessionRecord.STAGE_TYPE_REM) {
                        totalRemSleepDuration += stageDuration

                    }
                    // Light Sleep 시간인 경우
                    if (stage.stage == SleepSessionRecord.STAGE_TYPE_LIGHT) {
                        totalLightSleepDuration += stageDuration

                    }


                }

//                Log.i("ddd","깬 시간: ${totalAwakeDuration}")
//
//                Log.i("ddd","잠 종료 시간: ${sleepEnd}")
//
//                Log.i("ddd","잠 시작 시간: ${sleepStart}")
//
//                Log.i("ddd","분당 평균 호흡 수: ${ratesRate}")
//
//                Log.i("ddd","깊은 수면 시간: ${totalDeepSleepDuration}")
//
//                Log.i("ddd","잠 시간: ${durationMinutes + stageDuration }")
//
//                Log.i("ddd","분당 평균 심박동 수: ${heartRate.second}")
//
//                Log.i("ddd","분당 낮은 심박동 수: ${heartRate.first}")
//
//                Log.i("ddd","본 수면 여부: ")
//
//                Log.i("ddd","가벼운 수면 시간: ${totalLightSleepDuration}")
//
//                Log.i("ddd","램수면 시간: ${totalRemSleepDuration}")
//
//                Log.i("ddd","수면 시간: ${durationMinutes}")
//
//                csvData.add("${totalAwakeDuration},${sleepEnd},${sleepStart},${ratesRate},${totalDeepSleepDuration}" +
//                        ",${durationMinutes + stageDuration},${heartRate.second},${heartRate.first},${totalLightSleepDuration}" +
//                        ",${totalRemSleepDuration},${durationMinutes}")

            }

            appendToCSV(csvData, getSleepDownloadFilePath())
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


