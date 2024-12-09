package com.example.carbud.manufacturer.dto

data class ManufacturerRequest(
    val name: String,
    val models: MutableSet<String>
)
