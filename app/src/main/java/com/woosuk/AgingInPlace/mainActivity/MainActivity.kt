package com.woosuk.AgingInPlace.mainActivity


import android.Manifest
import android.annotation.SuppressLint
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
import android.widget.Button
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
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.wearable.Wearable
import com.woosuk.AgingInPlace.HealthConnectManager
import com.woosuk.AgingInPlace.R
import com.woosuk.AgingInPlace.loginActivity.LoginActivity
import com.woosuk.AgingInPlace.mainActivity.Calorie.CalorieActivity
import com.woosuk.AgingInPlace.mainActivity.Heartrate.HeartbeatActivity
import com.woosuk.AgingInPlace.mainActivity.Sleep.SleepActivity
import com.woosuk.AgingInPlace.restClient.models.ActivityDataVO
import com.woosuk.AgingInPlace.restClient.models.SleepDataVO
import com.google.android.material.navigation.NavigationView
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import com.woosuk.AgingInPlace.mainActivity.CIST.CistActivity
import com.woosuk.AgingInPlace.mainActivity.Workout.DementiaRiskActivity
import com.woosuk.AgingInPlace.medication.MedicationActivity
import com.woosuk.AgingInPlace.receiver.AlarmReceiver
import com.woosuk.AgingInPlace.receiver.MedicationAlarmReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.lang.reflect.Type
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.Calendar
import kotlin.random.Random


class MainActivity : AppCompatActivity() {
    private lateinit var firstTextView: TextView
    private lateinit var secondTextView: TextView
    private lateinit var mainLoginButton: ImageView
    private lateinit var menuButton: ImageView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
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

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getInt("userId", 0)
        setDailyAlarm()

        // 서버에서 알람 시간 가져오기
        fetchMedicationTime(userId)

        if (userId != 0) {
            sendIdToWear(userId)
        } else {
            Log.d("SendIdToWear", "userId is null")
        }

        requestNotificationPermission()

        if (intent.getBooleanExtra("trigger_functions", false)) {
            Log.d("MainActivity", "Triggering functions from intent")
            sendSleepAndThenTrain()
        }

        navView = findViewById(R.id.nav_view)
        mainLoginButton = findViewById(R.id.mainLoginButton)

        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        // 로그인 상태에 따라 로그인 버튼 가시성 조정
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
            drawerLayout.openDrawer(GravityCompat.START)
        }
        drawerLayout = findViewById(R.id.drawer_layout)

        navView = findViewById(R.id.nav_view)
        // 헤더 뷰 접근
        val headerView = navView.getHeaderView(0)

        // 로그인 상태에 따라 헤더의 버튼 가시성 조정
        val menuLoginBtn: Button = headerView.findViewById(R.id.menu_loginBtn)
        menuLoginBtn.visibility = if (isLoggedIn) View.GONE else View.VISIBLE

        menuLoginBtn.setOnClickListener {
            if (!isLoggedIn) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }

        val heartrateBtn = findViewById<CardView>(R.id.heartrate_btn)
        heartrateBtn.setOnClickListener {
            val intent = Intent(this@MainActivity, HeartbeatActivity::class.java)
            startActivity(intent)
        }

        val workoutBtn = findViewById<CardView>(R.id.workout_btn)
        workoutBtn.setOnClickListener {
            val intent = Intent(this@MainActivity, MedicationActivity::class.java)
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
        val cist_btn = findViewById<CardView>(R.id.cist_btn)
        cist_btn.setOnClickListener {
            val intent = Intent(this@MainActivity, CistActivity::class.java)
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

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_item1 -> {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://www.aginginplaces.net/"))
                    startActivity(intent)
                }
                R.id.nav_item2 -> {
                    val intent = Intent(this, UserInfoActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_item5 -> {
                    val intent = Intent(this, MedicationActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_item4 -> {
                    val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
                    if (isLoggedIn) {
                        logout()
                    } else {
                        val intent = Intent(this@MainActivity, LoginActivity::class.java)
                        startActivity(intent)
                    }
                }
                R.id.nav_item5 -> {
                    val intent = Intent(this@MainActivity, MedicationActivity::class.java)
                    startActivity(intent)
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        val nav_item4 = navView.menu.findItem(R.id.nav_item4)
        nav_item4.title = if (isLoggedIn) "로그아웃" else "로그인"

        // 로그인 상태에 따른 환영 메시지 업데이트
        updateWelcomeMessage()

        // 앱 실행 시 알람 시간 가져와서 알람 설정
        setupMedicationAlarm()


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

    private fun sendIdToWear(id: Int) {
        // 1. Message API를 통해 메시지 전송
        val messageClient = Wearable.getMessageClient(this)

        // 'id'를 바이트 배열로 변환하여 전송
        val message = id.toString().toByteArray()

        // 'nodeId'는 메시지를 보낼 대상 워치 장치의 ID
        Wearable.getNodeClient(this).connectedNodes
            .addOnSuccessListener { nodes ->
                if (nodes.isNotEmpty()) {
                    val nodeId = nodes[0].id // 첫 번째 연결된 노드의 ID를 사용
                    messageClient.sendMessage(nodeId, "/id_path", message)
                        .addOnSuccessListener { Log.d("MobileApp", "Message sent successfully with ID: $id") }
                        .addOnFailureListener { e -> Log.e("MobileApp", "Message failed to send", e) }
                } else {
                    Log.e("MobileApp", "No connected nodes found")
                }
            }
            .addOnFailureListener { e -> Log.e("MobileApp", "Failed to get connected nodes", e) }
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

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), PERMISSION_REQUEST_CODE)
            }
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    fun setDailyAlarm() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java).apply { action = "com.woosuk.AgingInPlace.ACTION_SEND_ALARM" }
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE  or PendingIntent.FLAG_IMMUTABLE)

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 18)
            set(Calendar.MINUTE,18)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            // 현재 시간이 자정 이후라면 다음 날 자정으로 설정
            if (before(Calendar.getInstance())) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }

        // 매일 자정에 반복
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
        Log.i("Set Alram", "Daily Alarm Set : ${calendar.time}")
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
    class InstantTypeAdapter : JsonSerializer<Instant>, JsonDeserializer<Instant> {
        override fun serialize(src: Instant, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return JsonPrimitive(src.toString()) // Instant를 ISO-8601 문자열로 변환
        }

        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Instant {
            return Instant.parse(json.asString) // ISO-8601 문자열을 Instant로 변환
        }
    }

    private fun sendSleepAndThenTrain() {
        Log.d("MainActivity", "sendSleepAndThenTrain called")
        sendSleep {
            sendTrain()
        }
    }

    fun sendTrain() {
        CoroutineScope(Dispatchers.IO).launch  {
            sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            val userId = sharedPreferences.getInt("userId", 0)
            if (!healthConnectManager.hasAllPermissions()) {
                Log.d("MainActivity", "HealthConnect permissions not granted")
                return@launch
            }

            // 한국 시간대 (KST) 지정
            val zoneKST = ZoneId.of("Asia/Seoul")

// 오늘 자정 (00:00:00 KST)
            val today = LocalDate.now(zoneKST)
            val startDateTime = LocalDateTime.of(today, LocalTime.MIDNIGHT)
            val dayStart = startDateTime.atZone(zoneKST).toInstant()

// 현재 시간 (KST)
            val currentDateTime = LocalDateTime.now(zoneKST)
            val dayEnd = currentDateTime.atZone(zoneKST).toInstant()


            val calActive = healthConnectManager.readCalActive(
                dayStart,
                dayEnd
            )

            val calTotal = healthConnectManager.readCalTotalRecords(
                dayStart,
                dayEnd
            )

            val totalDistance = healthConnectManager.readTotalDistance(dayStart, dayEnd)
            val totalDistanceInt = totalDistance?.toInt()?:0

            val steps = healthConnectManager.readTotalSteps(
                dayStart,
                dayEnd
            )
            val totalSteps = steps?.toInt() ?: 0

            val exerciseTime = healthConnectManager.readExerciseTime(
                dayStart,
                dayEnd
            )
            val bmr = healthConnectManager.readBMR(
                dayStart,
                dayEnd
            )

            val exerciseTimeTotal = exerciseTime?.times(60)?.toInt()
            val cal = calTotal.substring(0 until 6)
            val calTotalInt = cal.toDouble().toInt()


            val randomInt = Random.nextInt(0,201)

            // 더미 데이터
           /* val data = ActivityDataVO(userId
                ,"activity_data : $userId", Instant.now(), randomInt,
                randomInt, randomInt, Instant.now(), Instant.now(), randomInt, randomInt, randomInt, randomInt,
                randomInt, randomInt, randomInt, randomInt, randomInt, randomInt, false
            )*/

              val data = ActivityDataVO(userId
                  ,"activity_data : $userId", Instant.now(), calActive.toInt(),
                  calTotalInt, totalDistanceInt, dayEnd, dayStart, 0, 0, 0, 0,
                  0, 0, 0, 0, totalSteps, exerciseTimeTotal?:0, false
              )
//            데이터 전송
            postActivity(data, "http://3.39.236.95:8080/send/activity_data")

            Log.i("ddd", "사용자 ID : ${userId}")

            Log.i("ddd", "하루간 활동 칼로리: ${calActive}")

            Log.i("ddd", "하루간 총 사용 칼로리: ${calTotalInt}")

            Log.i("ddd", "매일 움직인 거리: ${totalDistanceInt}")

            Log.i("ddd", "측정 종료 시간: ${dayEnd}")

            Log.i("ddd", "측정 시작 시간: ${dayStart}")

            Log.i("ddd", "매일 걸음 수: ${steps}")

            Log.i("ddd", "활동 총 시간: ${exerciseTime}")

            Log.i("ddd", "BMR: ${bmr}")

        }
    }

    fun sendSleep(onComplete: () -> Unit) {
        Log.d("MainActivity", "sendSleep called")
        CoroutineScope(Dispatchers.IO).launch {
            sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            val userId = sharedPreferences.getInt("userId", 0)
            try {
                if (!healthConnectManager.hasAllPermissions()) {
                    Log.d("MainActivity", "HealthConnect permissions not granted")
                    return@launch
                }

                val yesterday = LocalDate.now(ZoneId.of("Asia/Seoul")).minusDays(1)
                val startDateTime = LocalDateTime.of(yesterday, LocalTime.of(20, 0, 0)) // 어제 오후 8시
                val startTime = startDateTime.toInstant(ZoneOffset.ofHours(9)) // KST (UTC+9)

                // 현재 시간
                val currentDateTime = LocalDateTime.now(ZoneId.of("Asia/Seoul"))
                val endTime = currentDateTime.toInstant(ZoneOffset.ofHours(9)) // KST (UTC+9)

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
                }
                val randomInt = Random.nextInt(0,201)
                val randomDouble = Random.nextDouble(0.0,201.0)
                // 더미 데이터
                /*val data = SleepDataVO(userId,
                    "sleep_data : $userId",  Instant.now(),
                    randomInt,  Instant.now(),  Instant.now(), randomDouble, randomInt, randomInt,randomInt, randomDouble, randomDouble,false,
                    randomInt,randomInt, randomInt,randomInt,randomInt,randomInt,randomInt,randomInt,randomInt,randomInt,randomInt, randomInt, true
                )*/

                 val data = SleepDataVO(userId,
                     "sleep_data : $userId",  Instant.now(),
                     awake, endTime, startTime, breathAverage, deep, durationMinutes,0, HRAverage, HRLowest,false,
                     light,0, rem,0,0,0,0,0,0,0,0, durationMinutes + stageDuration, true
                 )
                Log.i("MainActivity", "Sending sleep data: $data")
                Log.i("ddd", "사용자 ID : ${userId}")

                Log.i("ddd", "awkke : ${awake}")

                Log.i("ddd", "breathAverage: ${breathAverage}")

                Log.i("ddd", "deep: ${deep}")

                Log.i("ddd", "durationMinutes: ${durationMinutes}")

                Log.i("ddd", "HRAverage: ${HRAverage}")

                Log.i("ddd", "HRLowest: ${HRLowest}")

                Log.i("ddd", "total : ${durationMinutes + stageDuration}")

                // 데이터 전송
                postSleep(data, "http://3.39.236.95:8080/send/sleep_data")
                onComplete()
            } catch (e: Exception) {

                Log.e("MainActivity", "Error in sendSleep", e)
                onComplete() // 예외 발생 시에도 onComplete를 호출하여 sendTrain이 실행
            }
        }
    }

    // Gson 인스턴스 생성 시 커스텀 어댑터를 추가
    private val gson = GsonBuilder()
        .registerTypeAdapter(Instant::class.java, InstantTypeAdapter()) // Instant 어댑터 등록
        .create()

    fun postActivity(data: ActivityDataVO, url: String) {
        val jsonData = gson.toJson(data)
        val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
        val requestBody = jsonData.toRequestBody(mediaType)
        val client = OkHttpClient()

        val request = Request.Builder()
            .url(url)
            .addHeader("Content-Type", "application/json")
            .post(requestBody)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                Log.e("SendWorker", "Failed to send activity data: ${response.code}")
            } else {
                Log.d("SendWorker", "Data sent successfully: ${response.body?.string()}")
            }
        }
    }

    fun postSleep(data: SleepDataVO, url: String) {
        val jsonData = gson.toJson(data)
        val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
        val requestBody = jsonData.toRequestBody(mediaType)
        val client = OkHttpClient()

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                Log.e("SendWorker", "Failed to send sleep data: ${response.code}")
            } else {
                Log.d("SendWorker", "Data sent successfully: ${response.body?.string()}")
            }
        }
    }

    private fun setupMedicationAlarm() {
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getInt("userId",0)
        fetchMedicationTime(userId)
        val alarmTime = sharedPreferences.getString("alarm_time", "12:00") ?: "12:00"

        // 알람 설정 (MedicationActivity에서 사용했던 함수)
        scheduleMedicationAlarm(alarmTime)
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun scheduleMedicationAlarm(alarmTime: String) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, MedicationAlarmReceiver::class.java).apply {
            action = "com.woosuk.AgingInPlace.MEDICATION_ALARM"
        }
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val timeParts = alarmTime.split(":")
        val hour = timeParts[0].toInt()
        val minute = timeParts[1].toInt()

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)

            if (before(Calendar.getInstance())) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }

        // 알람 설정
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
        Log.i("Set Alarm", "Medication Alarm Set: ${calendar.time}")
    }
    private fun fetchMedicationTime(userId: Int) {
        val client = OkHttpClient()
        val requestBody = FormBody.Builder()
            .add("userId", userId.toString())
            .build()

        val request = Request.Builder()
            .url("http://3.39.236.95:8080/medication-time")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                runOnUiThread {
                    Log.e("MedicationActivity", "Failed to fetch data from server: ${e.message}")
                    showToast("서버와의 연결에 실패했습니다. 인터넷 연결을 확인하세요.")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    if (!response.isSuccessful) {
                        throw IOException("Unexpected code $response")
                    }

                    response.body?.let { responseBody ->
                        val jsonResponse = responseBody.string()
                        val jsonObject = JSONObject(jsonResponse)
                        val alarmTime = jsonObject.getString("alarmTime") // JSON에서 "alarmTime" 값을 추출

                        // "HH:mm:ss" 형식의 시간을 "HH:mm" 형식으로 변환
                        val formattedAlarmTime = alarmTime.substring(0, 5)

                        // SharedPreferences에 저장
                        saveAlarmTimeToSharedPreferences(formattedAlarmTime)

                    } ?: run {
                        runOnUiThread {
                            showToast("서버로부터 데이터를 받지 못했습니다.")
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    runOnUiThread {
                        Log.e("MedicationActivity", "Error processing server response: ${e.message}")
                        showToast("데이터를 처리하는 중 오류가 발생했습니다.")
                    }
                }
            }
        })
    }

    private fun saveAlarmTimeToSharedPreferences(alarmTime: String) {
        val editor = sharedPreferences.edit()
        editor.putString("alarm_time", alarmTime)
        editor.apply()
    }

    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }
}
