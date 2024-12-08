package com.example.carbud.vehicle

import com.example.carbud.vehicle.enums.FuelType
import com.example.carbud.vehicle.enums.Transmission
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class Vehicle(
    @Id
    val id: String? = null,
    val title: String,
    @Indexed
    val manufacturer: String,
    @Indexed
    val model: String,
    val distance: Int,
    val firstRegistration: Int,
    val fuelType: FuelType,
    val horsePower: Int,
    val kiloWats: Int,
    val transmission: Transmission,
    val numberOfOwners: Int,
    val color: String,
    val doorCount: Int,
    val price: Int,
    val sellerId: String
)
