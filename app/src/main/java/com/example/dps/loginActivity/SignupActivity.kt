package com.example.dps.loginActivity

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.dps.R

class SignupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val loginBackArrowImageView = findViewById<ImageView>(R.id.back_arrow)
        loginBackArrowImageView.setOnClickListener {
            onBackPressed()
        }

        var SignUpcheckBoxAll: CheckBox
        var SignUpcheckBox1: CheckBox
        var SignUpcheckBox2: CheckBox
        var SignUpcheckBox3: CheckBox

        SignUpcheckBoxAll = findViewById(R.id.SignUpcheckBoxAll)
        SignUpcheckBox1 = findViewById(R.id.SignUpcheckBox1)
        SignUpcheckBox2 = findViewById(R.id.SignUpcheckBox2)
        SignUpcheckBox3 = findViewById(R.id.SignUpcheckBox3)

        // 초기화 시 기본 아이콘 설정
        setInitialCheckboxIcon(SignUpcheckBoxAll)
        setInitialCheckboxIcon(SignUpcheckBox1)
        setInitialCheckboxIcon(SignUpcheckBox2)
        setInitialCheckboxIcon(SignUpcheckBox3)

        // 각 CheckBox에 대한 리스너 설정
        SignUpcheckBoxAll.setOnClickListener { onCheckChanged(SignUpcheckBoxAll) }
        SignUpcheckBox1.setOnClickListener { onCheckChanged(SignUpcheckBox1) }
        SignUpcheckBox2.setOnClickListener { onCheckChanged(SignUpcheckBox2) }
        SignUpcheckBox3.setOnClickListener { onCheckChanged(SignUpcheckBox3) }
    }

    private fun setInitialCheckboxIcon(checkBox: CheckBox) {
        // 초기 상태에서 기본 아이콘 설정
        checkBox.setButtonDrawable(R.drawable.baseline_radio_button_unchecked_24)
    }

    private fun onCheckChanged(compoundButton: CheckBox) {
        when (compoundButton.id) {
            R.id.SignUpcheckBoxAll -> {
                // 전체 선택/해제 버튼이 클릭된 경우
                val isChecked = compoundButton.isChecked
                findViewById<CheckBox>(R.id.SignUpcheckBox1).isChecked = isChecked
                findViewById<CheckBox>(R.id.SignUpcheckBox2).isChecked = isChecked
                findViewById<CheckBox>(R.id.SignUpcheckBox3).isChecked = isChecked
                updateCheckboxIcon(findViewById<CheckBox>(R.id.SignUpcheckBoxAll))
                updateCheckboxIcon(findViewById<CheckBox>(R.id.SignUpcheckBox1))
                updateCheckboxIcon(findViewById<CheckBox>(R.id.SignUpcheckBox2))
                updateCheckboxIcon(findViewById<CheckBox>(R.id.SignUpcheckBox3))
            }

            else -> {
                // 개별 체크박스가 클릭된 경우
                val checkBoxAll = findViewById<CheckBox>(R.id.SignUpcheckBoxAll)
                findViewById<CheckBox>(R.id.SignUpcheckBoxAll).isChecked = (
                        findViewById<CheckBox>(R.id.SignUpcheckBox1).isChecked
                                && findViewById<CheckBox>(R.id.SignUpcheckBox2).isChecked
                                && findViewById<CheckBox>(R.id.SignUpcheckBox3).isChecked)

                // 개별 체크박스 중 하나라도 unchecked 되면 전체 체크박스도 unchecked 되도록 처리
                if (!compoundButton.isChecked) {
                    checkBoxAll.isChecked = false
                    updateCheckboxIcon(checkBoxAll)
                } else {
                    // 모든 개별 체크박스가 체크되면 전체 체크박스도 체크되도록 처리
                    if (areAllCheckboxesChecked()) {
                        checkBoxAll.isChecked = true
                        updateCheckboxIcon(checkBoxAll)
                    }
                }

                updateCheckboxIcon(compoundButton)
            }
        }
    }

    private fun areAllCheckboxesChecked(): Boolean {
        val checkBox1 = findViewById<CheckBox>(R.id.SignUpcheckBox1)
        val checkBox2 = findViewById<CheckBox>(R.id.SignUpcheckBox2)
        val checkBox3 = findViewById<CheckBox>(R.id.SignUpcheckBox3)

        return checkBox1.isChecked && checkBox2.isChecked && checkBox3.isChecked
    }

    private fun updateCheckboxIcon(checkBox: CheckBox) {
        // 체크박스의 상태에 따라 아이콘 변경
        val iconResource = if (checkBox.isChecked) R.drawable.baseline_check_circle_24 else R.drawable.baseline_radio_button_unchecked_24
        checkBox.setButtonDrawable(iconResource)
    }
    fun onNextButtonClicked(view: android.view.View) {
        // 각 CheckBox의 상태 확인
        val checkBoxAll = findViewById<CheckBox>(R.id.SignUpcheckBoxAll)
        val checkBox1 = findViewById<CheckBox>(R.id.SignUpcheckBox1)
        val checkBox2 = findViewById<CheckBox>(R.id.SignUpcheckBox2)
        val checkBox3 = findViewById<CheckBox>(R.id.SignUpcheckBox3)

        // 전체 동의 여부 확인
        val isAllChecked = checkBoxAll.isChecked && checkBox1.isChecked && checkBox2.isChecked && checkBox3.isChecked

        if (!isAllChecked) {
            // 전체 동의하지 않은 경우 AlertDialog 표시
            showAlertDialog()
        } else {
            val signUpNextBtn = findViewById<Button>(R.id.signup_nextbtn)
            signUpNextBtn.setOnClickListener {
                val intent = Intent(this@SignupActivity, SignupActivity1::class.java)
                startActivity(intent)
            }
        }
    }
    private fun showAlertDialog() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("약관 동의")
        alertDialogBuilder.setMessage("약관에 모두 동의해주세요.")
        alertDialogBuilder.setPositiveButton("확인", DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
        alertDialogBuilder.show()
    }
}
