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
    private lateinit var idEdit: EditText
    private lateinit var username: String
    private lateinit var pwEdit: EditText
    private lateinit var pwcheckEdit: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup1)

        idEdit = findViewById(R.id.idEdit) // 아이디 입력란 초기화
        pwEdit = findViewById(R.id.passwordEdit)
        pwcheckEdit = findViewById(R.id.confirmPasswordEdit)

        val loginBackArrowImageView = findViewById<ImageView>(R.id.back_arrow)
        loginBackArrowImageView.setOnClickListener {
            onBackPressed()
        }

        val signUpNextBtn = findViewById<Button>(R.id.signup_nextbtn)
        signUpNextBtn.setOnClickListener {
            val password = pwEdit.text.toString()
            val confirmPassword = pwcheckEdit.text.toString()
            val enteredId = idEdit.text.toString() // 사용자가 입력한 아이디

            if (password.isEmpty() || confirmPassword.isEmpty() || enteredId.isEmpty()) {
                showEmptyFieldAlertDialog()
            } else if (password == confirmPassword) {
                val intent = Intent(this@SignupActivity1, SignupActivity2::class.java)
                intent.putExtra("USERNAME", enteredId) // 수정된 부분
                intent.putExtra("PASSWORD", password)
                startActivity(intent)
            } else {
                showPasswordMismatchAlertDialog()
            }
        }
    }

    private fun showEmptyFieldAlertDialog() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("입력 필요")
        alertDialogBuilder.setMessage("모든 필드를 입력해주세요.")
        alertDialogBuilder.setPositiveButton("확인") { dialog, which -> dialog.dismiss() }
        alertDialogBuilder.show()
    }

    private fun showPasswordMismatchAlertDialog() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("비밀번호 불일치")
        alertDialogBuilder.setMessage("비밀번호가 일치하지 않습니다. 다시 확인해주세요.")
        alertDialogBuilder.setPositiveButton("확인") { dialog, which -> dialog.dismiss() }
        alertDialogBuilder.show()
    }
}
