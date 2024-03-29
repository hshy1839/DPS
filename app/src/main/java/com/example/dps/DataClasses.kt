package com.example.dps

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

