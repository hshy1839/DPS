package com.example.dps.mainActivity


import ApiService
import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.lifecycle.lifecycleScope
import com.example.dps.HealthConnectManager
import com.example.dps.R
import com.example.dps.RetrofitClient
import com.example.dps.loginActivity.LoginActivity
import com.example.dps.mainActivity.Calorie.CalorieActivity
import com.example.dps.mainActivity.Heartrate.HeartbeatActivity
import com.example.dps.mainActivity.Sleep.SleepActivity
import com.example.dps.mainActivity.Workout.WorkoutActivity
import com.example.dps.receiver.MyBroadcastReceiver
import com.example.dps.restClient.models.ActivityDataVO
import com.example.dps.restClient.models.AvroRESTVO
import com.example.dps.restClient.models.SleepDataVO
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.Calendar

class MainActivity : AppCompatActivity() {
    private val retrofit = RetrofitClient.getInstance(this)
    private lateinit var firstTextView: TextView
    private lateinit var secondTextView: TextView
    private lateinit var mainLoginButton: ImageView
    private lateinit var menuButton: ImageView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var apiService: ApiService
    private lateinit var sharedPreferences: SharedPreferences
    private val PERMISSION_REQUEST_CODE = 100
    lateinit var healthConnectManager: HealthConnectManager
    private lateinit var requestPermissions: ActivityResultLauncher<Set<String>>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        healthConnectManager = HealthConnectManager(this)
        setContentView(R.layout.activity_main)
        createRequestPermissionsObject()
        checkAvailabilityAndPermissions()
        setDailyAlarm(this)

        requestNotificationPermission()


        if (intent.getBooleanExtra("trigger_functions", false)) {
            Log.d("MainActivity", "Triggering functions from intent")
            sendSleepAndThenTrain()
        }

        navView = findViewById(R.id.nav_view)

        mainLoginButton = findViewById(R.id.mainLoginButton)
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        // 만약 로그인되어 있다면 mainLoginButton을 숨깁니다.
        if (isLoggedIn) {
            mainLoginButton.visibility = View.GONE
        } else {
            mainLoginButton.visibility = View.VISIBLE
        }

        mainLoginButton.setOnClickListener {
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
        }

        menuButton = findViewById(R.id.menuButton)
        menuButton.setOnClickListener {
            // 메뉴 버튼을 클릭하면 Navigation Drawer를 열도록 함
            drawerLayout.openDrawer(GravityCompat.START)
        }

        navView = findViewById(R.id.nav_view)
        drawerLayout = findViewById(R.id.drawer_layout)

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
        val risk_btn = findViewById<CardView>(R.id.risk_btn)
        risk_btn.setOnClickListener {
            val intent = Intent(this@MainActivity, DementiaRiskActivity::class.java)
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
                    val intent = Intent(this@MainActivity, UserInfoActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_item3 -> {
                    menushowToast("설정 버튼")
                }
                R.id.nav_item4 -> {
                    logout()
                }
                R.id.nav_item5 -> {
                    val intent = Intent(this@MainActivity, MedicationActivity::class.java)
                    startActivity(intent)
                }
            }
            // 메뉴를 선택한 후에는 Drawer를 닫아줌
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

    }


    private fun logout() {
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", false)
        editor.commit()

        // 로그인 화면으로 이동합니다.
        val intent = Intent(this@MainActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
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
    private fun menushowToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    private fun sendSleepAndThenTrain() {
        Log.d("MainActivity", "sendSleepAndThenTrain called")
        sendSleep {
            sendTrain()
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
        // registerForActivityResult를 사용하여 권한 요청 결과를 처리하는 객체를 생성
         requestPermissions =
            registerForActivityResult(healthConnectManager.requestPermissionActivityContract) { granted ->
                // granted는 사용자가 부여한 권한 목록입니다.
                lifecycleScope.launch {
                    // granted 목록이 비어있지 않고 모든 권한이 부여되었는지 확인
                    if (granted.isNotEmpty() && healthConnectManager.hasAllPermissions()) {
                        // 모든 권한이 부여되었으면 Toast 메시지 표시
                        Toast.makeText(
                            this@MainActivity,
                            R.string.permission_granted,
                            Toast.LENGTH_SHORT,
                        ).show()
                    } else {
                        // 권한이 부여되지 않았으면 AlertDialog를 표시
                        AlertDialog.Builder(this@MainActivity)
                            .setMessage(R.string.permissions_not_granted)
                            .setPositiveButton("Ok", null)
                            .show()
                    }
                }
            }
    }
    private fun checkPermissions(): Job {
        return lifecycleScope.launch {
            try {
                if (!healthConnectManager.hasAllPermissions()) {
                    requestPermissions.launch(healthConnectManager.permissions)
                }
            } catch (exception: Exception) {
                Log.e("ddd", exception.toString())
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
                        "not_supported_description",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
                return false
            }

            HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED -> {
                runOnUiThread {
                    Toast.makeText(
                        this@MainActivity,
                        "not_installed_description",
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
    fun sendTrain() {
        CoroutineScope(Dispatchers.IO).launch  {

            if (!healthConnectManager.hasAllPermissions()) {
                Log.d("MainActivity", "HealthConnect permissions not granted")
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


//             활동 데이터 인스턴스 생성
            val data = ActivityDataVO(
                "Lee1234567", "운동_이석영", Instant.now(),activeCalrorie,
                calTotalInt, dailyMovement.substring(0 until 3).toInt(), dayEnd, dayStart, 0, 0, 0, 10,
                0, 0, 100, 100, steps!!.toInt(), exerciseTime!!.toInt(), false
            )
//            데이터 전송
            post(data, "http://3.34.218.215:8082/topics/activity_data/")

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

 fun sendSleep(onComplete: () -> Unit) {
        Log.d("MainActivity", "sendSleep called")
        lifecycleScope.launch {
            try {
                if (!healthConnectManager.hasAllPermissions()) {
                    Log.d("MainActivity", "HealthConnect permissions not granted")
                    return@launch
                }

                val twoMonthsAgo = LocalDate.now(ZoneId.of("Asia/Seoul")).minusMonths(3)
                val startDateTime = LocalDateTime.of(twoMonthsAgo, LocalTime.of(0, 0, 1))
                val startTime = startDateTime.toInstant(ZoneOffset.UTC)
                val today = LocalDate.now(ZoneId.of("Asia/Seoul"))
                val endDateTime = LocalDateTime.of(today, LocalTime.of(23, 59, 59))
                val endTime = endDateTime.toInstant(ZoneOffset.UTC)

                val sleepRecords = healthConnectManager.readSleep(startTime, endTime)

                var awake = 0
                var bedTimeEnd: Instant = Instant.now()
                var bedTimeStart: Instant = Instant.now()
                var breathAverage = 0.0
                var deep = 0
                var stageDuration = 0
                var HRAverage = 0.0
                var HRLowest = 0.0
                var light = 0
                var rem = 0
                var durationMinutes = 0

                for (sleepRecord in sleepRecords) {
                    val rates = healthConnectManager.readRespiratoryRateRecords(
                        sleepRecord.startTime,
                        sleepRecord.endTime
                    )
                    val heartRate = healthConnectManager.readHeartRateAggregate(
                        sleepRecord.startTime,
                        sleepRecord.endTime
                    )
                    HRAverage = heartRate.first
                    HRLowest = heartRate.second

                    for (rate in rates) {
                        breathAverage = rate.rate
                    }

                    for (stage in sleepRecord.stages) {
                        bedTimeStart = sleepRecord.startTime.atZone(ZoneId.of("Asia/Seoul")).toInstant()
                        bedTimeEnd = sleepRecord.endTime.atZone(ZoneId.of("Asia/Seoul")).toInstant()
                        durationMinutes = Duration.between(bedTimeStart, bedTimeEnd).toMinutes().toInt()

                        val stageStart = stage.startTime.atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime()
                        val stageEnd = stage.endTime.atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime()
                        stageDuration = Duration.between(stageStart, stageEnd).toMinutes().toInt()

                        when (stage.stage) {
                            SleepSessionRecord.STAGE_TYPE_AWAKE, SleepSessionRecord.STAGE_TYPE_AWAKE_IN_BED -> awake += stageDuration
                            SleepSessionRecord.STAGE_TYPE_DEEP -> deep += stageDuration
                            SleepSessionRecord.STAGE_TYPE_REM -> rem += stageDuration
                            SleepSessionRecord.STAGE_TYPE_LIGHT -> light += stageDuration
                        }
                    }
                    Log.i("ddd","${bedTimeEnd}")
                    Log.i("ddd","${bedTimeStart}")
                    Log.i("ddd","${rem}")


                    val data = SleepDataVO(
                        "Test9", "이석영", Instant.now(),
                         awake,
                        bedTimeEnd, bedTimeStart, breathAverage, deep, durationMinutes, HRAverage, HRLowest, light,
                        rem, durationMinutes + stageDuration, true
                    )
                    Log.i("MainActivity", "Sending sleep data: $data")

                    // 데이터 전송
                    post(data, "http://3.34.218.215:8082/topics/sleep_data/")
                }
                onComplete()
            } catch (e: Exception) {
                Log.e("MainActivity", "Error in sendSleep", e)
                onComplete() // 예외 발생 시에도 onComplete를 호출하여 sendTrain이 실행되도록 합니다.
            }
        }
    }

    fun post(data: AvroRESTVO, apiURL: String?) {
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
                    var message: String?
                    while (reader.readLine().also { message = it } != null) {
                        buffer.append(message).append("\n")
                    }
                } else {
                    buffer.append("code : ")
                    buffer.append(conn.responseCode).append("\n")
                    buffer.append("message : ")
                    buffer.append(conn.responseMessage).append("\n")
                }
                withContext(Dispatchers.Main) {
                    println(buffer.toString())
                }
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
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), PERMISSION_REQUEST_CODE)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Log.d("Permission", "Notification permission granted")
            } else {
                Log.d("Permission", "Notification permission denied")
            }
        }
    }
    fun setDailyAlarm(context: Context) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 16)
            set(Calendar.MINUTE, 10)
            set(Calendar.SECOND, 0)
        }

        if (calendar.timeInMillis < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, MyBroadcastReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )

        Log.d("Alarm", "Alarm set for: ${calendar.time}")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Log.d("Permissions", "Health Connect permissions granted")
            } else {
                Log.d("Permissions", "Health Connect permissions denied")
                Toast.makeText(this, "Health Connect permissions are required for the app to function properly", Toast.LENGTH_LONG).show()
            }
        }
    }



}


