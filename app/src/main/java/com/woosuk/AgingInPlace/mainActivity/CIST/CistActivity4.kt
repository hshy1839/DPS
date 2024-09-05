package com.woosuk.AgingInPlace.mainActivity.CIST

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.woosuk.AgingInPlace.R

class CistActivity4 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cist4)

        // startTextBtn 버튼을 찾아서 변수에 저장
        val nextButton = findViewById<Button>(R.id.nextButton)
        val prevButton = findViewById<Button>(R.id.prevButton)
        // 버튼 클릭 리스너 추가
        nextButton.setOnClickListener {
            val intent = Intent(
                this@CistActivity4,
                CistActivity5::class.java
            )
            startActivity(intent)
        }
        prevButton.setOnClickListener {
            val intent = Intent(
                this@CistActivity4,
                CistActivity3::class.java
            )
            startActivity(intent)
        }
    }
}