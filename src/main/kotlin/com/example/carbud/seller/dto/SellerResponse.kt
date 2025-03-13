package com.example.carbud.seller.dto

import com.example.carbud.vehicle.dto.VehicleInfoResponse

data class SellerResponse(
    val id: String?,
    val firstName: String?,
    val lastName: String?,
    val userName: String,
    val phoneNumber: String,
    val email: String,
    val location: String,
    val vehicles: List<VehicleInfoResponse> = emptyList()
)
