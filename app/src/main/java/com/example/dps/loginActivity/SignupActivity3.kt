package com.example.dps.loginActivity

import ApiService
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dps.R
import com.example.dps.RetrofitClient
import com.example.dps.UserData
import com.example.dps.loginActivity.SignupActivity4
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class SignupActivity3 : AppCompatActivity() {
    private lateinit var nameEdit: EditText
    private lateinit var emailEdit: EditText
    private lateinit var phoneNumberEdit: EditText
    private lateinit var birthdayEdit: EditText
    private lateinit var roleRadio: RadioGroup
    private lateinit var genderRadio: RadioGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup3)

        val username = intent.getStringExtra("USERNAME")
        val password = intent.getStringExtra("PASSWORD")
        nameEdit = findViewById(R.id.nameEdit)
        emailEdit = findViewById(R.id.emailEdit)
        phoneNumberEdit = findViewById(R.id.phoneNumberEdit)
        birthdayEdit = findViewById(R.id.birthdayEdit)
        roleRadio = findViewById(R.id.roleRadio)
        genderRadio = findViewById(R.id.genderRadio)

        val sdf = SimpleDateFormat("yyyyMMdd", Locale.getDefault())

        birthdayEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                try {
                    val birthdayString = s.toString()
                    if (birthdayString.length == 8) {
                        val birthdayDate = sdf.parse(birthdayString)
                        // birthdayDate를 사용하여 원하는 작업을 수행
                    }
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
            }
        })



        val signUpNextBtn = findViewById<Button>(R.id.signup_nextbtn)
        signUpNextBtn.setOnClickListener {
            val selectedRoleId = roleRadio.checkedRadioButtonId
            val selectedGenderId = genderRadio.checkedRadioButtonId

            val selectedRole = findViewById<RadioButton>(selectedRoleId).text.toString()
            val selectedGender = findViewById<RadioButton>(selectedGenderId).text.toString()

            val apiService = RetrofitClient.getInstance().create(ApiService::class.java)
            val call = apiService.signup(
                UserData(
                    username,
                    password,
                    emailEdit.text.toString(),
                    nameEdit.text.toString(),
                    sdf.parse(birthdayEdit.text.toString()),
                    selectedGender,
                    phoneNumberEdit.text.toString(),
                    selectedRole
                )
            )
            call.enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        // 서버에서 정상적으로 응답을 받았을 때의 처리
                        Toast.makeText(applicationContext, "데이터가 성공적으로 전송되었습니다.", Toast.LENGTH_SHORT).show()

                        // 서버 응답에 따라 다음 액티비티로 이동
                        val intent = Intent(this@SignupActivity3, SignupActivity4::class.java)
                        startActivity(intent)
                    } else {
                        // 서버에서 응답이 실패했을 때의 처리
                        Toast.makeText(applicationContext, "데이터 전송에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    // 네트워크 오류 등으로 전송에 실패했을 때의 처리
                    Toast.makeText(applicationContext, "네트워크 오류: " + t.message, Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
