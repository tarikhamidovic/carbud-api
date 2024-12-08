package com.example.carbud.seller

import com.example.carbud.vehicle.VehicleInfo
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
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
    @Indexed
    val userId: String,
    val vehicles: MutableList<VehicleInfo> = mutableListOf()
)
