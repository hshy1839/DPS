package com.example.dps.loginActivity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.dps.R

class SignupActivity1 : AppCompatActivity() {
    private lateinit var editTextId: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup1)

        val loginBackArrowImageView = findViewById<ImageView>(R.id.back_arrow)
        loginBackArrowImageView.setOnClickListener {
            onBackPressed()
        }

        editTextId = findViewById(R.id.idEditbox)

        val signUpNextBtn = findViewById<Button>(R.id.signup_nextbtn)
        signUpNextBtn.setOnClickListener {
            onNextButtonClicked()
        }
    }

    private fun onNextButtonClicked() {
        // 아이디 입력란 확인
        val enteredId = editTextId.text.toString()

        if (enteredId.isEmpty()) {
            showEmptyIdAlertDialog()
        } else {
            val intent = Intent(this@SignupActivity1, SignupActivity2::class.java)
            startActivity(intent)
        }
    }

    private fun showEmptyIdAlertDialog() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("아이디 입력")
        alertDialogBuilder.setMessage("아이디를 입력해주세요.")
        alertDialogBuilder.setPositiveButton("확인") { dialog, which -> dialog.dismiss() }
        alertDialogBuilder.show()
    }
}
