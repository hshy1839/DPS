package com.woosuk.AgingInPlace.mainActivity.CIST

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.woosuk.AgingInPlace.R

class CistActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cist)

        // startTextBtn 버튼을 찾아서 변수에 저장
        val startButton = findViewById<Button>(R.id.startTextBtn)
        // 버튼 클릭 리스너 추가
        startButton.setOnClickListener {
            val intent = Intent(
                this@CistActivity,
                CistActivity1::class.java
            )
            startActivity(intent)
        }
    }
}