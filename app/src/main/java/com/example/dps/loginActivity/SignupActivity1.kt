package com.example.dps.loginActivity

import ApiService
import android.content.Intent
import com.example.dps.R
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.dps.RetrofitClient
import com.example.dps.UserData
import com.example.dps.loginActivity.SignupActivity2
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SignupActivity1 : AppCompatActivity() {
    private lateinit var idEdit: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup1)
        idEdit = findViewById(R.id.idEdit)

        val loginBackArrowImageView = findViewById<ImageView>(R.id.back_arrow)
        loginBackArrowImageView.setOnClickListener {
            onBackPressed()
        }

        val signUpNextBtn = findViewById<Button>(R.id.signup_nextbtn)
        signUpNextBtn.setOnClickListener {
            val username = idEdit.text.toString() // 사용자가 입력한 데이터
            val intent = Intent(this@SignupActivity1, SignupActivity2::class.java)
            intent.putExtra("USERNAME", username)
            startActivity(intent)
            onNextButtonClicked()
        }
    }
    private fun onNextButtonClicked() {
        // 아이디 입력란 확인
        val enteredId = idEdit.text.toString()

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
