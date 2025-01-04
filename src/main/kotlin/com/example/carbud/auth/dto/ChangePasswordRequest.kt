package com.example.carbud.auth.dto

data class ChangePasswordRequest(
    val oldPassword: String,
    val newPassword: String
)
