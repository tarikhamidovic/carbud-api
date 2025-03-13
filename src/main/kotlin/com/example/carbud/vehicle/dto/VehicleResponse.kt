package com.example.carbud.vehicle.dto

data class VehicleResponse(
    val id: String?,
    val title: String,
    val manufacturer: String,
    val model: String,
    val distance: Int,
    val firstRegistration: Int,
    val fuelType: String,
    val horsePower: Int,
    val kiloWats: Int,
    val transmission: String,
    val numberOfOwners: Int,
    val color: String,
    val doorCount: Int,
    val price: Int
)