package com.example.dps.receiver
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.dps.mainActivity.MainActivity

//class BootReceiver : BroadcastReceiver() {
//    override fun onReceive(context: Context, intent: Intent) {
//        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
//            val mainActivityIntent = Intent(context, MainActivity::class.java).apply {
//                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                putExtra("set_alarm", true)
//            }
//            context.startActivity(mainActivityIntent)
//        }
//    }
//}
