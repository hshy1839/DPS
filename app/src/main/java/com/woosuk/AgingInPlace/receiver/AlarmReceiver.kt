package com.woosuk.AgingInPlace.receiver

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.woosuk.AgingInPlace.R
import com.woosuk.AgingInPlace.mainActivity.MainActivity
import java.util.Calendar

class AlarmReceiver : BroadcastReceiver() {
    @SuppressLint("ScheduleExactAlarm")
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("BroadcastReceiver", "Alarm received")

        // 알림 생성 함수 호출
        createNotificationChannel(context)
        showNotification(context)
        // 다음 날 같은 시간에 다시 알람 설정
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val newIntent = Intent(context, AlarmReceiver::class.java).apply {
            action = "com.woosuk.AgingInPlace.ACTION_SEND_ALARM"
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context, 1, newIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 17)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            // 다음 날 같은 시간으로 설정
            add(Calendar.DAY_OF_MONTH, 1)
        }

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )

        Log.i("Set Alarm", "Next SEND Alarm Set: ${calendar.time}")
    }


    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "DailyAlarmChannel"
            val descriptionText = "Aging In Place"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("DAILY_ALARM_CHANNEL", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showNotification(context: Context) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("trigger_functions", true)
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = NotificationCompat.Builder(context, "DAILY_ALARM_CHANNEL")
            .setSmallIcon(R.drawable.dps_logo) // 알림 아이콘 설정
            .setContentTitle("Aging In Place")
            .setContentText("모바일 알림을 클릭해 데이터를 전송해주세요.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent) // 알림 클릭 시 실행할 인텐트 설정
            .setAutoCancel(true) // 알림 클릭 시 자동으로 알림을 제거

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            with(NotificationManagerCompat.from(context)) {
                notify(1001, builder.build()) // 알림 표시
            }
        } else {
            Log.d("BroadcastReceiver", "Notification permission not granted")
        }
    }
}