package com.woosuk.AgingInPlace.mainActivity.Workout

import com.woosuk.AgingInPlace.loginActivity.LoginActivity
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
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
import com.woosuk.AgingInPlace.mainActivity.MainActivity
import com.woosuk.AgingInPlace.mainActivity.UserInfoActivity
import com.woosuk.AgingInPlace.medication.MedicationActivity

class WorkoutActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var navView: NavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout)

        val loginButton = findViewById<ImageView>(R.id.loginButton)
        loginButton.setOnClickListener {
            val intent = Intent(this@WorkoutActivity, LoginActivity::class.java)
            startActivity(intent)
        }

        val lineChart = findViewById<LineChart>(R.id.lineChart)
        setupLineChart(lineChart)

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
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        drawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        // 토글 버튼을 추가하여 메뉴가 열리고 닫히도록 함
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

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
                R.id.nav_item3 -> {
                    val intent = Intent(this, MedicationActivity::class.java)
                    startActivity(intent)
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
        val nav_item4 = navView.menu.findItem(R.id.nav_item4)
        nav_item4.title = if (isLoggedIn) "로그아웃" else "로그인"

        val menuButton = findViewById<ImageView>(R.id.menuButton_heartbeat)
        menuButton.setOnClickListener {
            // 메뉴 버튼을 클릭하면 Navigation Drawer를 열도록 함
            drawerLayout.openDrawer(GravityCompat.START)
        }
        updateWelcomeMessage()

        val backArrow = findViewById<ImageView>(R.id.back_arrow)
        backArrow.setOnClickListener {
            onBackPressed()
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
        xAxis.valueFormatter = IndexAxisValueFormatter(
            arrayOf(
                "1월",
                "2월",
                "3월",
                "4월",
                "5월",
                "6월",
                "7월",
                "8월",
                "9월",
                "10월",
                "11월",
                "12월"
            )
        )
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
        val dataSet = LineDataSet(entries, "일별 운동량")
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
        val dataSet = BarDataSet(entries, "월별 운동량")
        dataSet.color = Color.parseColor("#5271FE")
        dataSet.valueTextColor = ContextCompat.getColor(this, R.color.black)

        // BarData 생성 및 설정
        val barData = BarData(dataSet)
        barData.setDrawValues(true)

        // BarChart에 데이터 추가
        barChart.data = barData
        barChart.invalidate()
    }
}

