package com.example.dps

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
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
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.material.navigation.NavigationView

class SleepActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
}


