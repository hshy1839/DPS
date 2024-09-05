package com.woosuk.AgingInPlace.mainActivity.CIST

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.woosuk.AgingInPlace.R

class CistActivity3 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cist3)

        // startTextBtn 버튼을 찾아서 변수에 저장
        val nextButton = findViewById<Button>(R.id.nextButton)
        val prevButton = findViewById<Button>(R.id.prevButton)
        // 버튼 클릭 리스너 추가
        nextButton.setOnClickListener {
            val intent = Intent(
                this@CistActivity3,
                CistActivity4::class.java
            )
            startActivity(intent)
        }
        prevButton.setOnClickListener {
            val intent = Intent(
                this@CistActivity3,
                CistActivity2::class.java
            )
            startActivity(intent)
        }
    }
}