package com.example.carbud.seller

import com.example.carbud.seller.dto.SellerResponse

fun Seller.toResponse() = SellerResponse(
    firstName = firstName,
    lastName = lastName,
    userName = userName,
    phoneNumber = phoneNumber,
    email = email,
    location = location,
    vehicles = vehicles
)