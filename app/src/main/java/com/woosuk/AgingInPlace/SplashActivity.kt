package com.woosuk.AgingInPlace

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import com.woosuk.AgingInPlace.mainActivity.MainActivity


class SplashActivity : AppCompatActivity() {
    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash)
        Handler().postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            finish()
        }, 1500)
    }
}