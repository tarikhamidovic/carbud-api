package com.example.carbud.seller

import com.example.carbud.seller.dto.SellerRequest
import com.example.carbud.seller.dto.SellerResponse
import com.example.carbud.vehicle.toResponse

fun Seller.toResponse() = SellerResponse(
    id = id,
    firstName = firstName,
    lastName = lastName,
    userName = userName,
    phoneNumber = phoneNumber,
    email = email,
    location = location,
    vehicles = vehicles.map { it.toResponse() }
)

fun SellerRequest.toEntity(userId: String) = Seller(
    firstName = firstName,
    lastName = lastName,
    userName = userName,
    phoneNumber = phoneNumber,
    email = email,
    location = location,
    userId = userId
)
