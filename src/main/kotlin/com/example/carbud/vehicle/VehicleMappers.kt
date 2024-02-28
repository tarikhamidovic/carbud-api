package com.example.carbud.vehicle

import com.example.carbud.vehicle.dto.VehicleRequest
import com.example.carbud.vehicle.dto.VehicleResponse
import com.example.carbud.vehicle.enums.FuelType
import com.example.carbud.vehicle.enums.Transmission


fun Vehicle.toResponse() = VehicleResponse(
    title = title,
    manufacturer = manufacturer,
    model = model,
    distance = distance,
    firstRegistration = firstRegistration,
    fuelType = fuelType.name.lowercase().replaceFirstChar { it.uppercase() },
    horsePower = horsePower,
    kiloWats = kiloWats,
    transmission = transmission.name.lowercase().replaceFirstChar { it.uppercase() },
    numberOfOwners = numberOfOwners,
    color = color,
    doorCount = doorCount,
    price = price,
    features = features
)

fun VehicleRequest.toEntity() = Vehicle(
    title = title,
    manufacturer = manufacturer,
    model = model,
    distance = distance,
    firstRegistration = firstRegistration,
    fuelType = FuelType.valueOf(fuelType.uppercase()),
    horsePower = horsePower,
    kiloWats = kiloWats,
    transmission = Transmission.valueOf(transmission.uppercase()),
    numberOfOwners = numberOfOwners,
    color = color,
    doorCount = doorCount,
    price = price,
    features = features
)