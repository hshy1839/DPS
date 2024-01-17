package com.example.dps


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val loginButton = findViewById<Button>(R.id.loginButton)
        loginButton.setOnClickListener { // LoginActivity로 전환
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}