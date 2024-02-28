package com.example.carbud.seller

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class Seller(
    @Id
    val id: String? = null,
    val firstName: String?,
    val lastName: String?,
    val userName: String,
    val phoneNumber: String,
    val email: String,
    val location: String,
    val userId: String,
    val vehicles: List<String> = emptyList()
)
