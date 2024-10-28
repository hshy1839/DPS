package com.woosuk.AgingInPlace.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.app.AlarmManager
import android.app.PendingIntent
import java.util.Calendar

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.i("BootReceiver", "Device rebooted - resetting alarm")

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val alarmIntent = Intent(context, AlarmReceiver::class.java).apply {
                action = "com.woosuk.AgingInPlace.ACTION_SEND_ALARM"
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context, 0, alarmIntent, PendingIntent.FLAG_IMMUTABLE  or PendingIntent.FLAG_UPDATE_CURRENT
            )
            alarmManager.cancel(pendingIntent)

            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 17)
                set(Calendar.MINUTE, 30)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)

                if (before(Calendar.getInstance())) {
                    add(Calendar.DAY_OF_MONTH, 1)
                }
            }

            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
            Log.i("BootReceiver", "Alarm reset for: ${calendar.time}")
        }
    }
}
