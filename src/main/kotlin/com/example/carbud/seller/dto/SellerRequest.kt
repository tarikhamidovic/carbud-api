package com.example.carbud.seller.dto

data class SellerRequest(
    val firstName: String?,
    val lastName: String?,
    val userName: String,
    val phoneNumber: String,
    val email: String,
    val location: String
)
