package com.woosuk.AgingInPlace.mainActivity.CIST

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.woosuk.AgingInPlace.R

class CistActivity1 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cist1)

        // startTextBtn 버튼을 찾아서 변수에 저장
        val nextButton = findViewById<Button>(R.id.nextButton)
        // 버튼 클릭 리스너 추가
        nextButton.setOnClickListener {
            val intent = Intent(
                this@CistActivity1,
                CistActivity2::class.java
            )
            startActivity(intent)
        }
    }
}