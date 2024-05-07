package com.example.dps

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.records.BasalMetabolicRateRecord
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.RespiratoryRateRecord
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.SleepSessionRecord.Companion.STAGE_TYPE_AWAKE
import androidx.health.connect.client.records.SleepSessionRecord.Companion.STAGE_TYPE_DEEP
import androidx.health.connect.client.records.SleepSessionRecord.Companion.STAGE_TYPE_LIGHT
import androidx.health.connect.client.records.SleepSessionRecord.Companion.STAGE_TYPE_REM
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import java.time.Instant

class HealthConnectManager(private val context: Context) {
    private val healthConnectClient by lazy { HealthConnectClient.getOrCreate(context) }

    val permissions = setOf(
        HealthPermission.getWritePermission(SleepSessionRecord::class),
        HealthPermission.getReadPermission(SleepSessionRecord::class),
        HealthPermission.getReadPermission(HeartRateRecord::class),
        HealthPermission.getReadPermission(ActiveCaloriesBurnedRecord::class),
        HealthPermission.getReadPermission(TotalCaloriesBurnedRecord::class),
        HealthPermission.getReadPermission(DistanceRecord::class),
        HealthPermission.getReadPermission(StepsRecord::class),
        HealthPermission.getReadPermission(ExerciseSessionRecord::class),
        HealthPermission.getReadPermission(RespiratoryRateRecord::class)

    )

    suspend fun hasAllPermissions(): Boolean {
        if (HealthConnectClient.getSdkStatus(context) != HealthConnectClient.SDK_AVAILABLE) {
            return false
        }
        return healthConnectClient.permissionController.getGrantedPermissions()
            .containsAll(permissions)
    }

    val requestPermissionActivityContract by lazy { PermissionController.createRequestPermissionResultContract() }

    suspend fun readCalActive(start: Instant, end: Instant): String {
        val response = healthConnectClient.aggregate(
            AggregateRequest(
                metrics = setOf(ActiveCaloriesBurnedRecord.ACTIVE_CALORIES_TOTAL),
                timeRangeFilter = TimeRangeFilter.between(start, end)
            )
        )
        return response[ActiveCaloriesBurnedRecord.ACTIVE_CALORIES_TOTAL].toString()
    }

    suspend fun readDistance(start: Instant, end: Instant): List<DistanceRecord> {
        val response =
            healthConnectClient.readRecords(
                ReadRecordsRequest<DistanceRecord>(
                    timeRangeFilter = TimeRangeFilter.between(start, end)
                )
            )
        return response.records
    }

    suspend fun readCalTotalRecords(start: Instant, end: Instant): String {
        val response = healthConnectClient.aggregate(
            AggregateRequest(
                metrics = setOf(TotalCaloriesBurnedRecord.ENERGY_TOTAL),
                timeRangeFilter = TimeRangeFilter.between(start, end)
            )
        )
        return response[TotalCaloriesBurnedRecord.ENERGY_TOTAL].toString()
    }
    suspend fun readTotalDistance(start: Instant, end: Instant): String {
        val response = healthConnectClient.aggregate(
            AggregateRequest(
                metrics = setOf(DistanceRecord.DISTANCE_TOTAL),
                timeRangeFilter = TimeRangeFilter.between(start, end)
            )
        )
        return response[DistanceRecord.DISTANCE_TOTAL]?.inMeters.toString()
    }
    suspend fun readTotalSteps(start: Instant, end: Instant): Long? {
        val response = healthConnectClient.aggregate(
            AggregateRequest(
                metrics = setOf(StepsRecord.COUNT_TOTAL),
                timeRangeFilter = TimeRangeFilter.between(start, end)
            )
        )
        return response[StepsRecord.COUNT_TOTAL]
    }

    suspend fun readExerciseTime(start: Instant, end: Instant): Long? {
        val response = healthConnectClient.aggregate(
            AggregateRequest(
                metrics = setOf(ExerciseSessionRecord.EXERCISE_DURATION_TOTAL),
                timeRangeFilter = TimeRangeFilter.between(start, end)
            )
        )
        val duration = response[ExerciseSessionRecord.EXERCISE_DURATION_TOTAL]
        return duration?.toMillis()?.div(60000) // 변환된 값을 분으로 나눕니다.
    }
    suspend fun readBMR(start: Instant, end: Instant): String {
        val response = healthConnectClient.aggregate(
            AggregateRequest(
                metrics = setOf(BasalMetabolicRateRecord.BASAL_CALORIES_TOTAL),
                timeRangeFilter = TimeRangeFilter.between(start, end)
            )
        )
        return response[BasalMetabolicRateRecord.BASAL_CALORIES_TOTAL].toString()
    }

    suspend fun readSleep(start: Instant, end: Instant): List<SleepSessionRecord> {
        val response =
            healthConnectClient.readRecords(
                ReadRecordsRequest<SleepSessionRecord>(
                    timeRangeFilter = TimeRangeFilter.between(start, end)
                )
            )
        return response.records
    }

    suspend fun readHeartRate(start: Instant, end: Instant): List<HeartRateRecord> {
        val response =
            healthConnectClient.readRecords(
                ReadRecordsRequest<HeartRateRecord>(
                    timeRangeFilter = TimeRangeFilter.between(start, end)
                )
            )
        return response.records
    }

    suspend fun readHeartRateAggregate(start: Instant, end: Instant): Pair<Double, Double> {
        val response = healthConnectClient.aggregate(
            AggregateRequest(
                metrics = setOf(HeartRateRecord.BPM_AVG,HeartRateRecord.BPM_MIN),
                timeRangeFilter = TimeRangeFilter.between(start, end)
            )
        )
        val averageHeartRate = response[HeartRateRecord.BPM_AVG]
        val minimumHeartRate = response[HeartRateRecord.BPM_MIN]
        return Pair(averageHeartRate!!.toDouble(), minimumHeartRate!!.toDouble())
    }

    suspend fun readRespiratoryRateRecords(start: Instant, end: Instant): List<RespiratoryRateRecord> {
        val response =
            healthConnectClient.readRecords(
                ReadRecordsRequest<RespiratoryRateRecord>(
                    timeRangeFilter = TimeRangeFilter.between(start, end)
                )
            )
        return response.records
    }
    companion object {
        val stageToEnumMap: Map<String, Int> =
            mapOf(
                "Awake" to STAGE_TYPE_AWAKE,
                "Light" to STAGE_TYPE_LIGHT,
                "Deep" to STAGE_TYPE_DEEP,
                "REM" to STAGE_TYPE_REM,
            )
        val enumToStageMap: Map<Int, String> =
            mapOf(
                STAGE_TYPE_AWAKE to "Awake",
                STAGE_TYPE_LIGHT to "Light",
                STAGE_TYPE_DEEP to "Deep",
                STAGE_TYPE_REM to "REM",
            )
    }
}