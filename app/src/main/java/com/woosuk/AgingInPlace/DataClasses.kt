package com.woosuk.AgingInPlace

import android.media.Image
import org.json.JSONArray
import java.util.Date

data class UserData(
    val userId: Int,
    val email: String,
    val name: Date,
    val birthdate: String,
    val gender: String,
    val phoneNumber: String,
    val role: String,
    val medication: String,
    val medicationTime: String,
    val diagnosis: String,
)


data class SignupData(
    val username: String,
    val password: String,
    val email: String,
    val name: String,
    val birthdate: String,
    val gender: String,
    val phoneNumber: String,
    val role: String,
)


data class LoginData(
    val username: String,
    val password: String
)

data class MedicationResponse(
    val medications: List<Medication>
)

data class Medication(
    val medications: String,
    val disease: String
)

data class CistQuestionResponse(
    val question_text: String,
    val title: String,
    val type: String,
    val id: Int,
    val image_url: String
)

data class CistResponseData(
    val userId: Int,
    val questionId: Int,
    val responses: String
)
