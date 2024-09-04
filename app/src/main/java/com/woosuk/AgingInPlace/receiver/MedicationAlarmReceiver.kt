package com.woosuk.AgingInPlace.receiver

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.woosuk.AgingInPlace.R
import com.woosuk.AgingInPlace.medication.MedicationActivity
import java.util.Calendar

class MedicationAlarmReceiver : BroadcastReceiver() {
    @SuppressLint("ScheduleExactAlarm")
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "com.woosuk.AgingInPlace.MEDICATION_ALARM") {
            Log.i("MedicationAlarmReceiver", "Medication 알람입니다.")

            // 알림을 보여주거나 추가적인 작업 처리
            sendNotification(context)

            // SharedPreferences에서 저장된 알람 시간을 불러오기
            val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            val alarmTime = sharedPreferences.getString("alarm_time", "12:00") ?: "12:00"

            // 알람 시간 설정
            val timeParts = alarmTime.split(":")
            val hour = timeParts[0].toInt()
            val minute = timeParts[1].toInt()

            // 다음 알람 설정
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val newIntent = Intent(context, MedicationAlarmReceiver::class.java).apply {
                action = "com.woosuk.AgingInPlace.MEDICATION_ALARM"
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context, 0, newIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val calendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)

                // 다음 날 같은 시간으로 설정
                add(Calendar.DAY_OF_MONTH, 1)
            }

            // 매일 자정에 반복
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )

            Log.i("Set Alarm", "Next Exact Alarm Set: ${calendar.time}")
        }
    }

    private fun sendNotification(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        Log.i("MedicationAlarmReceiver", "sendNotification 호출됨")
        // Android 8.0 이상에서는 NotificationChannel이 필요
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "medication_channel"
            val channelName = "Medication Alerts"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = "Channel for medication reminders"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, "medication_channel")
            .setSmallIcon(R.drawable.dps_logo)  // 아이콘이 존재하는지 확인 필요
            .setContentTitle("Aging In Place")
            .setContentText("약물 복용 시간입니다.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(1, notification)
    }
}

