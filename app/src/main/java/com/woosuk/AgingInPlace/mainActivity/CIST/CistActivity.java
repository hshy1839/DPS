package com.woosuk.AgingInPlace.mainActivity.CIST;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.dps.R;

public class CistActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cist); // 해당하는 레이아웃 파일 이름

        // startTextBtn 버튼을 찾아서 변수에 저장
        Button startButton = findViewById(R.id.startTextBtn);

        // 버튼 클릭 리스너 추가
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cist1Activity로 이동하는 인텐트 생성
                Intent intent = new Intent(CistActivity.this, CistActivity1.class);
                startActivity(intent);
            }
        });
    }
}