package com.example.dps

data class UserData(
    val userId: Int,
    val email: String,
    val name: String,
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

