package com.woosuk.AgingInPlace.mainActivity.Workout

import ApiService
import android.annotation.SuppressLint
import android.content.Context

import com.woosuk.AgingInPlace.loginActivity.LoginActivity

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
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
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.material.navigation.NavigationView
import com.google.gson.JsonObject
import com.woosuk.AgingInPlace.R
import com.woosuk.AgingInPlace.RetrofitClient
import com.woosuk.AgingInPlace.mainActivity.MainActivity

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

import org.json.JSONArray
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DementiaRiskActivity :AppCompatActivity(){

    private lateinit var drawerLayout:DrawerLayout
    private lateinit var sharedPreferences:SharedPreferences
    private lateinit var navView:NavigationView
    private lateinit var sleepIcon: ImageView
    private lateinit var calorieIcon: ImageView
    private lateinit var stepIcon: ImageView
    private lateinit var sleepText: TextView
    private lateinit var calorieText: TextView
    private lateinit var cistScoreText: TextView
    private lateinit var stepTextView: TextView
    private lateinit var apiService: ApiService
    private lateinit var activity_link: TextView
    private lateinit var sleep_duration_link: TextView
    private lateinit var dementia_prob_text: TextView
    private var userId: Int = 0

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState:Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dementia_risk)
        sharedPreferences=getSharedPreferences("MyPrefs",Context.MODE_PRIVATE)
        // View 초기화

        navView=findViewById(R.id.nav_view)
        sleepIcon = findViewById(R.id.sleep_duration_icon)
        stepIcon = findViewById(R.id.step_icon)
        calorieIcon = findViewById(R.id.calorie_icon)
        sleepText = findViewById(R.id.sleep_duration_text)
        calorieText = findViewById(R.id.calorie_text)
        stepTextView = findViewById(R.id.step_text)
        activity_link = findViewById(R.id.activity_link)
        sleep_duration_link = findViewById(R.id.sleep_duration_link)
        apiService = RetrofitClient.getInstance(this).create(ApiService::class.java)
        dementia_prob_text = findViewById(R.id.dementia_prob_text)
        cistScoreText = findViewById(R.id.cist_score_text)
        userId = sharedPreferences.getInt("userId", 0)
        val sleepIconName = sharedPreferences.getString("sleepDurationIcon", "ic_good")
        val calorieIconName = sharedPreferences.getString("calorieIcon", "ic_good")
        val sleepTextName = sharedPreferences.getString("sleepDurationMessage", "상태가 없습니다.")
        val calorieTextName = sharedPreferences.getString("calorieMessage", "상태가 없습니다.")

        //fetchCistScore(userId)
        fetchDataFromApi("http://3.39.236.95:8080/chart/steps", userId)
        fetchDataFromApiCalories("http://3.39.236.95:8080/chart/calories", userId)
        fetchDataFromDurationApi("http://3.39.236.95:8080/chart/duration", userId)
        fetchDataFromProbApi("http://3.39.236.95:8080/wear/prob", userId)

/*
        if (sleepIconName == "ic_bad") {
            sleepIcon.setImageResource(R.drawable.ic_bad)
            sleepText.text = sleepTextName
        } else {
            sleepIcon.setImageResource(R.drawable.ic_good)

            sleepText.text = sleepTextName
        }
        if (calorieIconName == "ic_bad") {
            calorieIcon.setImageResource(R.drawable.ic_bad)
            calorieText.text = calorieTextName
        } else {
            calorieIcon.setImageResource(R.drawable.ic_good)
            calorieText.text = calorieTextName
        }*/

        val loginButton=findViewById<ImageView>(R.id.loginButton)
        loginButton.setOnClickListener{
            val intent=Intent(this@DementiaRiskActivity,LoginActivity::class.java)
            startActivity(intent)
        }

        val backArrow=findViewById<ImageView>(R.id.back_arrow)
        backArrow.setOnClickListener{
            onBackPressed()
        }

        var id=getUserId()

        val isLoggedIn=sharedPreferences.getBoolean("isLoggedIn",false)
        drawerLayout=findViewById(R.id.drawer_layout)
        val navView:NavigationView=findViewById(R.id.nav_view)

        // 헤더 뷰 접근
        val headerView=navView.getHeaderView(0)

        // 로그인 상태에 따라 헤더의 버튼 가시성 조정
        loginButton.visibility=if(isLoggedIn)View.GONE else View.VISIBLE

        loginButton.setOnClickListener{
            if(!isLoggedIn){
                val intent=Intent(this,LoginActivity::class.java)
                startActivity(intent)
            }
        }

        // 토글 버튼을 추가하여 메뉴가 열리고 닫히도록 함
        val toggle=ActionBarDrawerToggle(
            this,drawerLayout,R.string.navigation_drawer_open,R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        sharedPreferences=getSharedPreferences("MyPrefs",Context.MODE_PRIVATE)

        // 네비게이션 메뉴 아이템 클릭 리스너 설정
        navView.setNavigationItemSelectedListener{menuItem->
            when(menuItem.itemId){
                R.id.nav_item1->{
                    // Menu 1 선택 시의 동작
                    val intent=Intent(Intent.ACTION_VIEW,Uri.parse("http://www.aginginplaces.net/"))
                    startActivity(intent)
                }
                R.id.nav_item2->{
                    // Menu 2 선택 시의 동작
                    showToast("고객센터 이동 버튼")
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

        val menuButton=findViewById<ImageView>(R.id.menuButton_heartbeat)
        menuButton.setOnClickListener{
            // 메뉴 버튼을 클릭하면 Navigation Drawer를 열도록 함
            drawerLayout.openDrawer(GravityCompat.START)
        }
        updateWelcomeMessage()
    }

    private fun fetchCistScore(userId: Int) {
        val call = apiService.getCistScore(userId)
        val empty = "상태가 없습니다.";
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    val jsonObject = response.body()
                    if (jsonObject != null) {
                        // mmse 값을 안전하게 가져오기
                        val mmseElement = jsonObject.get("mmse")

                        if (mmseElement != null && !mmseElement.isJsonNull) {
                            // mmse를 정수로 가져오기
                            val mmse = if (mmseElement.isJsonPrimitive && mmseElement.asJsonPrimitive.isNumber) {
                                mmseElement.asInt // 정수로 가져오기
                            } else {
                                Log.e("InvalidData", "mmse is not a valid number")
                                return
                            }

                            // cistScoreText에 정수형 mmse 값을 문자열로 설정
                            cistScoreText.text = mmse.toString()
                        } else {
                            Log.e("EmptyData", "mmse is null or not found")
                            cistScoreText.text = empty
                        }
                    } else {
                        Log.e("EmptyData", "JsonObject is null")
                    }
                } else {
                    Log.e("API", "Request failed: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.e("API", "Network error: ${t.message}")
            }
        })
    }


    private fun updateWelcomeMessage() {
        // NavigationView의 헤더 가져오기
        val headerView = navView.getHeaderView(0)
        val welcomeTextView: TextView = headerView.findViewById(R.id.welcome_textView)

        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        val name = sharedPreferences.getString("name", "")

        if (isLoggedIn) {
            welcomeTextView.text = "안녕하세요 $name"
        } else {
            welcomeTextView.text = "로그인 후 사용해주세요"
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


    override fun onBackPressed(){
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            // 만약 Navigation Drawer가 열려 있다면, 닫기
            drawerLayout.closeDrawer(GravityCompat.START)
        }else{
            // 그렇지 않으면 기본 동작 수행
            super.onBackPressed()
        }
    }

    private fun showToast(message:String){
        Toast.makeText(applicationContext,message,Toast.LENGTH_SHORT).show()
    }
    private fun saveUserId(userId:String){
        val sharedPreferences=getSharedPreferences("AppPrefs",Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("userId",userId).apply()
    }

    private fun getUserId():Int{
        val sharedPreferences=getSharedPreferences("MyPrefs",Context.MODE_PRIVATE)
        return sharedPreferences.getInt("userId",0)
    }


    private fun post(apiURL:String,userId:Int):String{
        val client=OkHttpClient()

        val formBody=FormBody.Builder()
            .add("userId",userId.toString())
            .build()

        val request=Request.Builder()
            .url(apiURL)
            .post(formBody)
            .build()

        client.newCall(request).execute().use{response->
            if(!response.isSuccessful)throw IOException("Unexpected code $response")
            return response.body?.string()?:""
        }
    }

    private fun fetchDataFromApi(url: String, userId: Int) {
        val client = OkHttpClient()
        val formBody = FormBody.Builder().add("userId", userId.toString()).build()

        val request = Request.Builder()
            .url(url)
            .post(formBody)
            .build()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    response.body?.string()?.let { jsonData ->
                        withContext(Dispatchers.Main) {
                            parseJsonDataForStepCharts(jsonData)
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        showToast("Failed to fetch data: ${response.message}")
                    }
                }
            } catch (e: IOException) {
                Log.e("CalorieActivity", "Network error: ${e.localizedMessage}")
                withContext(Dispatchers.Main) {
                    showToast("Network error: ${e.localizedMessage}")
                }
            }
        }
    }

    private fun fetchDataFromApiCalories(apiURL: String, userId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val jsonResponse = post(apiURL, userId)
                withContext(Dispatchers.Main) {
                    parseJsonDataForCalorieCharts(jsonResponse)
                }
            } catch (e: Exception) {
                Log.e("Activity", "Error fetching data", e)
            }
        }
    }

    private fun fetchDataFromDurationApi(apiURL: String, userId: Int) {
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
    private fun fetchDataFromProbApi(apiURL: String, userId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d("FetchData", "Sending request to $apiURL with user_id $userId")
                val jsonResponse = probPost(apiURL, userId)
                withContext(Dispatchers.Main) {
                    parseJsonDataForProb(jsonResponse)
                }
            } catch (e: Exception) {
                Log.e("FetchData", "Error fetching data: ${e.localizedMessage}", e)
            }
        }
    }

    private suspend fun probPost(apiURL: String, userId: Int): String {
        val client = OkHttpClient()
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val jsonBody = """{"user_id": $userId}"""
        val requestBody = jsonBody.toRequestBody(mediaType)

        val request = Request.Builder()
            .url(apiURL)
            .post(requestBody)
            .build()

        client.newCall(request).execute().use { response ->
            val responseBody = response.body?.string()
            Log.d("FetchData", "Response code: ${response.code}, body: $responseBody")
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            return responseBody ?: throw IOException("Empty response body")
        }
    }


    private fun parseJsonDataForProb(jsonData: String) {
        try {
            val dataArray = JSONArray(jsonData)
            for (i in 0 until dataArray.length()) {
                val item = dataArray.getJSONObject(i)
                val username = item.getString("username")
                val dementiaProb = item.getInt("dementia_prob")

                updateDementiaProbUI(username, dementiaProb)
            }
        } catch (e: JSONException) {
            Log.e("ProbActivity", "Error parsing JSON data", e)
        }
    }

    private fun parseJsonDataForStepCharts(jsonData: String) {
        try {
            val dataArray = JSONArray(jsonData)
            var totalSteps = 0f
            var count = 0

            for (i in 0 until dataArray.length()) {
                val item = dataArray.getJSONObject(i)
                val steps = item.getInt("steps").toFloat()
                totalSteps += steps
                count++
            }

            if (count > 0) {
                val averageSteps = totalSteps / count
                updateStepAverageUI(averageSteps)
            } else {
            }
        } catch (e: JSONException) {
            Log.e("StepActivity", "Error parsing JSON data", e)
            showToast("Error parsing data: ${e.localizedMessage}")
        }
    }

    private fun parseJsonDataForCalorieCharts(jsonData: String) {
        try {
            val dataArray = JSONArray(jsonData)
            var totalCalories = 0f
            var count = 0

            for (i in 0 until dataArray.length()) {
                val item = dataArray.getJSONObject(i)
                val calTotal = item.getInt("calTotal").toFloat()
                totalCalories += calTotal
                count++
            }

            if (count > 0) {
                val averageCalories = totalCalories / count
                updateCalorieAverageUI(averageCalories)
            } else {
            }
        } catch (e: JSONException) {
            Log.e("CalorieActivity", "Error parsing JSON data", e)
        }
    }

    private fun parseJsonDataForDurationCharts(jsonData: String) {
        try {
            val dataArray = JSONArray(jsonData)
            var totalSleep = 0f
            var count = 0

            for (i in 0 until dataArray.length()) {
                val item = dataArray.getJSONObject(i)
                val sleepTotal = item.getInt("duration").toFloat()
                totalSleep += sleepTotal
                count++
            }

            if (count > 0) {
                val averageSleepDuration = totalSleep / count
                updateDurationAverageUI(averageSleepDuration)
            } else {
            }
        } catch (e: JSONException) {
            Log.e("SleepActivity", "Error parsing JSON data", e)
        }
    }
    private fun updateDementiaProbUI(username: String, dementiaProb: Int) {
        dementia_prob_text.text = "${username}님의 치매 위험도는 $dementiaProb%입니다."
        Log.d("ProbActivity", "Username: $username, Dementia Probability: $dementiaProb")
    }

    private fun updateStepAverageUI(averageSteps: Float) {
        stepTextView.text = averageSteps.toString()
        if(averageSteps.toInt()>2500){
            stepIcon.setImageResource(R.drawable.ic_good)
        }else{
            stepIcon.setImageResource(R.drawable.ic_bad)
            activity_link.visibility = View.VISIBLE
            activity_link.text =  Html.fromHtml("<a href='http://www.aginginplaces.net/contents'>여기를 클릭하여 동영상을 시청해주세요.</a>")
            activity_link.movementMethod = LinkMovementMethod.getInstance()
        }
        Log.d("StepActivity", "Average Steps: $averageSteps")
    }

    @SuppressLint("SetTextI18n")
    private fun updateCalorieAverageUI(averageCalories: Float) {
        calorieText.text = averageCalories.toInt().toString() + "Kcal"
        if(averageCalories.toInt()>155){
            calorieIcon.setImageResource(R.drawable.ic_good)
        }else{
            calorieIcon.setImageResource(R.drawable.ic_bad)
            activity_link.text = "http://www.aginginplaces.net/contents"
            activity_link.visibility = View.VISIBLE

        }
        Log.d("CalorieActivity", "Average Calories: $averageCalories")
    }
    private fun updateDurationAverageUI(averageSleepDuration: Float) {
        sleepText.text = (averageSleepDuration.toInt() / 60).toString() + "시간"
        if((averageSleepDuration * 60).toInt() < 35449){
            sleepIcon.setImageResource(R.drawable.ic_good)
        }else{
            sleep_duration_link.text = "http://www.aginginplaces.net/contents"
            sleepIcon.setImageResource(R.drawable.ic_bad)
            sleep_duration_link.visibility = View.VISIBLE
        }
        Log.d("SleepActivity", "Average Sleep Duration: $averageSleepDuration")
    }
}

