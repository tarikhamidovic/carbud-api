package com.example.carbud.manufacturer.dto

data class ManufacturerResponse(
    val name: String,
    val models: MutableSet<String>
)
