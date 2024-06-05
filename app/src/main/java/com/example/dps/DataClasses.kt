package com.example.dps

import java.util.Date

data class UserData(
    val userId: String,
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

