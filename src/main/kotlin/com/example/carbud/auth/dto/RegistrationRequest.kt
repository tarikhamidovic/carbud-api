package com.example.carbud.auth.dto

data class RegistrationRequest(
    val username: String,
    val password: String,
    val firstName: String?,
    val lastName: String?,
    val phoneNumber: String,
    val email: String,
    val location: String
)
