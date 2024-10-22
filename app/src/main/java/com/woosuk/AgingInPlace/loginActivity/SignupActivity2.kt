package com.woosuk.AgingInPlace.loginActivity

import ApiService
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.woosuk.AgingInPlace.R
import com.woosuk.AgingInPlace.RetrofitClient
import com.woosuk.AgingInPlace.SignupData
import com.google.gson.JsonObject
import com.woosuk.AgingInPlace.mainActivity.CIST.CistActivity1
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class SignupActivity2 : AppCompatActivity() {
    private lateinit var nameEdit: EditText
    private lateinit var emailEdit: EditText
    private lateinit var phoneNumberEdit: EditText
    private lateinit var birthdayEdit: EditText
    private lateinit var roleRadio: RadioGroup
    private lateinit var genderRadio: RadioGroup
    private lateinit var backArrow: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup2)

        var username = intent.getStringExtra("USERNAME")
        var password = intent.getStringExtra("PASSWORD")
        nameEdit = findViewById(R.id.nameEdit)
        emailEdit = findViewById(R.id.emailEdit)
        phoneNumberEdit = findViewById(R.id.phoneNumberEdit)
        backArrow = findViewById(R.id.back_arrow)
        birthdayEdit = findViewById(R.id.birthdayEdit)
        roleRadio = findViewById(R.id.roleRadio)
        genderRadio = findViewById(R.id.genderRadio)

        val sdfInput = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val sdfOutput = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        birthdayEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                try {
                    val birthdayString = s.toString()
                    if (birthdayString.length == 8) {
                        val birthdayDate = sdfInput.parse(birthdayString)
                        // birthdayDate를 사용하여 원하는 작업을 수행
                    }
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
            }
        })

        backArrow.setOnClickListener {
            val intent = Intent(
                this@SignupActivity2,
                SignupActivity1::class.java
            )
            startActivity(intent)
        }

        val signUpNextBtn = findViewById<Button>(R.id.signup_nextbtn)
        signUpNextBtn.setOnClickListener {
            val selectedRoleId = roleRadio.checkedRadioButtonId
            val selectedGenderId = genderRadio.checkedRadioButtonId

            if (selectedRoleId == -1 || selectedGenderId == -1) {
                Toast.makeText(applicationContext, "역할과 성별을 선택해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val selectedRole = findViewById<RadioButton>(selectedRoleId).text.toString()
            val selectedGender = findViewById<RadioButton>(selectedGenderId).text.toString()

            val birthdayString = birthdayEdit.text.toString()
            val birthdayDate: Date
            val formattedDate: String

            try {
                birthdayDate = sdfInput.parse(birthdayString)
                formattedDate = sdfOutput.format(birthdayDate)
            } catch (e: ParseException) {
                Toast.makeText(applicationContext, "잘못된 생일 형식입니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val apiService = RetrofitClient.getInstance(this).create(ApiService::class.java)

            if (username.isNullOrEmpty() || password.isNullOrEmpty() ||
                emailEdit.text.isNullOrEmpty() || nameEdit.text.isNullOrEmpty() ||
                formattedDate.isNullOrEmpty() || selectedGender.isNullOrEmpty() ||
                phoneNumberEdit.text.isNullOrEmpty() || selectedRole.isNullOrEmpty()) {
                Toast.makeText(applicationContext, "모든 항목을 입력하세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val signupData = SignupData(
                username!!,
                password!!,
                emailEdit.text.toString(),
                nameEdit.text.toString(),
                formattedDate,
                selectedGender,
                phoneNumberEdit.text.toString(),
                selectedRole,
            )

            val call = apiService.signup(signupData)
            call.enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful) {
                        Toast.makeText(applicationContext, "데이터가 성공적으로 전송되었습니다.", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@SignupActivity2, SignupActivity3::class.java)
                        startActivity(intent)
                    } else {
                        Log.e("API Error", "Code: ${response.code()}, Message: ${response.message()}")
                        Toast.makeText(applicationContext, "데이터 전송에 실패했습니다. 응답 코드: ${response.code()}, 메시지: ${response.message()}", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Log.e("API Error", "통신 실패: ${t.message}", t)
                    Toast.makeText(applicationContext, "다시 작성해주세요: " + t.message, Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}