package com.example.dps.mainActivity


import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.health.connect.client.HealthConnectClient
import androidx.lifecycle.lifecycleScope
import com.example.dps.HealthConnectManager
import com.example.dps.loginActivity.LoginActivity
import com.example.dps.R
import com.example.dps.mainActivity.Calorie.CalorieActivity
import com.example.dps.mainActivity.Heartrate.HeartbeatActivity
import com.example.dps.mainActivity.Sleep.SleepActivity
import com.example.dps.mainActivity.Workout.WorkoutActivity
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

private const val APP_TAG = "Aging In Place"

class MainActivity : AppCompatActivity() {

    private lateinit var firstTextView: TextView
    private lateinit var secondTextView: TextView
    private lateinit var drawerLayout: DrawerLayout

    private lateinit var healthConnectManager: HealthConnectManager
    private lateinit var requestPermissions: ActivityResultLauncher<Set<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        healthConnectManager = HealthConnectManager(this)
        createRequestPermissionsObject()
        checkAvailabilityAndPermissions()

        val loginButton = findViewById<ImageView>(R.id.loginButton)
        loginButton.setOnClickListener {
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
        }

        val heartrateBtn = findViewById<CardView>(R.id.heartrate_btn)
        heartrateBtn.setOnClickListener {
            val intent = Intent(this@MainActivity, HeartbeatActivity::class.java)
            startActivity(intent)
        }

        val workoutBtn = findViewById<CardView>(R.id.workout_btn)
        workoutBtn.setOnClickListener {
            val intent = Intent(this@MainActivity, WorkoutActivity::class.java)
            startActivity(intent)
        }

        val sleepBtn = findViewById<CardView>(R.id.sleep_btn)
        sleepBtn.setOnClickListener {
            val intent = Intent(this@MainActivity, SleepActivity::class.java)
            startActivity(intent)
        }

        val calorieBtn = findViewById<CardView>(R.id.calorie_btn)
        calorieBtn.setOnClickListener {
            val intent = Intent(this@MainActivity, CalorieActivity::class.java)
            startActivity(intent)
        }

        firstTextView = findViewById(R.id.firstTextView)
        secondTextView = findViewById(R.id.secondTextView)

        firstTextView.alpha = 0f
        secondTextView.alpha = 0f

        firstTextView.animate()
            .alpha(1f)
            .setDuration(3000)
            .withStartAction { }
            .withEndAction {
                secondTextView.animate()
                    .alpha(1f)
                    .setDuration(1000)
                    .start()
            }
            .start()

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

        val menuButton = findViewById<ImageView>(R.id.menuButton)
        menuButton.setOnClickListener {
            // 메뉴 버튼을 클릭하면 Navigation Drawer를 열도록 함
            drawerLayout.openDrawer(GravityCompat.START)
        }

    }

    private fun checkAvailabilityAndPermissions() {
        if (!checkAvailability()) {
            return
        }
        lifecycleScope.launch {
            if (!healthConnectManager.hasAllPermissions()) {
                checkPermissions()
            }
        }
    }

    private fun createRequestPermissionsObject() {
        requestPermissions =
            registerForActivityResult(healthConnectManager.requestPermissionActivityContract) { granted ->
                lifecycleScope.launch {
                    if (granted.isNotEmpty() && healthConnectManager.hasAllPermissions()) {
                        Toast.makeText(
                            this@MainActivity,
                            R.string.permission_granted,
                            Toast.LENGTH_SHORT,
                        ).show()
                    } else {
                        AlertDialog.Builder(this@MainActivity)
                            .setMessage(R.string.permissions_not_granted)
                            .setPositiveButton("Ok", null)
                            .show()
                    }
                }
            }
    }
    private fun aa(){

    }

    private fun checkPermissions(): Job {
        return lifecycleScope.launch {
            try {
                if (!healthConnectManager.hasAllPermissions()) {
                    requestPermissions.launch(healthConnectManager.permissions)
                }
            } catch (exception: Exception) {
                Log.e(APP_TAG, exception.toString())
                Toast.makeText(this@MainActivity, "Error: $exception", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun checkAvailability(): Boolean {
        when (HealthConnectClient.getSdkStatus(this)) {
            HealthConnectClient.SDK_UNAVAILABLE -> {
                runOnUiThread {
                    Toast.makeText(
                        this@MainActivity,
                        "권한이 없습니다.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
                return false
            }

            HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED -> {
                runOnUiThread {
                    Toast.makeText(
                        this@MainActivity,
                        "헬스 커넥트가 설치되어있지 않습니다.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
                try {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=com.google.android.apps.healthdata"),
                        ),
                    )
                } catch (e: ActivityNotFoundException) {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.healthdata"),
                        ),
                    )
                }
                return false
            }
            else -> {
                return true
            }
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

    override fun onResume() {
        super.onResume()
        startAnimation()
    }

    private fun startAnimation() {
        firstTextView.alpha = 0f
        secondTextView.alpha = 0f

        firstTextView.animate()
            .alpha(1f)
            .setDuration(1000)
            .withStartAction { }
            .withEndAction {
                secondTextView.animate()
                    .alpha(1f)
                    .setDuration(1000)
                    .start()
            }
            .start()

    }
}
