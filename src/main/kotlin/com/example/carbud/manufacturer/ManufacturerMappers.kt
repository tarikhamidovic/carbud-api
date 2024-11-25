package com.example.carbud.manufacturer

import com.example.carbud.manufacturer.dto.ManufacturerResponse

fun Manufacturer.toResponse() = ManufacturerResponse(
    name = name,
    models = models
)
