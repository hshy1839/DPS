package com.example.dps

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.material.navigation.NavigationView

class HeartbeatActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_heartbeat)

        val loginButton = findViewById<ImageView>(R.id.loginButton)
        loginButton.setOnClickListener {
            val intent = Intent(this@HeartbeatActivity, LoginActivity::class.java)
            startActivity(intent)
        }

        val lineChart = findViewById<LineChart>(R.id.lineChart)
        setupLineChart(lineChart)

        // 시간별 심박수 데이터 추가 (예시)
        val entries = mutableListOf<Entry>()
        entries.add(Entry(1f, 80f))
        entries.add(Entry(2f, 85f))
        entries.add(Entry(3f, 90f))
        entries.add(Entry(4f, 88f))
        entries.add(Entry(5f, 95f))
        addDataToLineChart(lineChart, entries)

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
                    showToast("Select Menu 1")
                }
                R.id.nav_item2 -> {
                    // Menu 2 선택 시의 동작
                    showToast("Select Menu 2")
                }
                R.id.nav_item3 -> {
                    // Menu 3 선택 시의 동작
                    showToast("Select Menu 3")
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
    private fun setupLineChart(lineChart: LineChart) {
        // LineChart 설정
        lineChart.setTouchEnabled(true)
        lineChart.setPinchZoom(true)
        lineChart.description = Description().apply { text = "시간" }

        // X 축 설정
        val xAxis = lineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)

        // Y 축 설정
        val leftAxis = lineChart.axisLeft
        leftAxis.setDrawGridLines(false)
        leftAxis.setDrawZeroLine(false)

        val rightAxis = lineChart.axisRight
        rightAxis.isEnabled = false

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

    private fun addDataToLineChart(lineChart: LineChart, entries: List<Entry>) {
        // LineDataSet 생성
        val dataSet = LineDataSet(entries, "심박수")
        dataSet.color = ContextCompat.getColor(this, R.color.black)
        dataSet.valueTextColor = ContextCompat.getColor(this, R.color.black)

        // LineData 생성 및 설정
        val lineData = LineData(dataSet)
        lineData.setDrawValues(true)

        // LineChart에 데이터 추가
        lineChart.data = lineData
        lineChart.invalidate()
    }
}


