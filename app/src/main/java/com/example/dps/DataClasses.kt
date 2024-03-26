package com.example.dps

import java.util.Date

data class UserData(
    val username: String?,
    val password: String?,
    val email: String,
    val name: String,
    val birthdate: Date,
    val gender: String,
    val phoneNumber: String,
    val role: String
)

data class LoginData(
    val username: String,
    val password: String
)