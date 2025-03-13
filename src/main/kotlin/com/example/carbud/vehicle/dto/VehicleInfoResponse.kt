package com.example.carbud.vehicle.dto

data class VehicleInfoResponse(
    val id: String?,
    val title: String,
    val manufacturer: String,
    val model: String,
    val distance: Int,
    val firstRegistration: Int,
    val price: Int
)
