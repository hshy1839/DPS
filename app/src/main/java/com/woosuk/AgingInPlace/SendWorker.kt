package com.woosuk.AgingInPlace

import android.content.ContentValues.TAG
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.woosuk.AgingInPlace.restClient.models.ActivityDataVO
import com.woosuk.AgingInPlace.restClient.models.SleepDataVO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.sql.Timestamp
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset




class SendWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {
    private lateinit var healthConnectManager : HealthConnectManager
    private lateinit var sharedPreferences: SharedPreferences
    init {
        sharedPreferences = appContext.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
    }

    override suspend fun doWork(): Result {
        healthConnectManager = HealthConnectManager(applicationContext)
        Log.i(TAG, "SendWorker running")
        withContext(Dispatchers.IO) {
        }
        return Result.success()
    }
}
