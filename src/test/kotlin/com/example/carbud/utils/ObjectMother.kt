package com.example.carbud.utils

import com.example.carbud.auth.User
import com.example.carbud.auth.dto.LoginRequest
import com.example.carbud.auth.dto.RegistrationRequest
import com.example.carbud.auth.enums.Role
import com.example.carbud.manufacturer.Manufacturer
import com.example.carbud.seller.Seller
import com.example.carbud.seller.dto.SellerRequest
import com.example.carbud.seller.toResponse
import com.example.carbud.vehicle.*
import com.example.carbud.vehicle.dto.VehicleRequest
import com.example.carbud.vehicle.enums.FuelType
import com.example.carbud.vehicle.enums.Transmission

object ObjectMother {

    val vehicle = Vehicle(
        id = "abc123",
        title = "test title",
        manufacturer = "test manufacturer",
        model = "test model",
        distance = 100,
        firstRegistration = 2020,
        fuelType = FuelType.PETROL,
        horsePower = 100,
        kiloWats = 100,
        transmission = Transmission.AUTOMATIC,
        numberOfOwners = 1,
        color = "black",
        doorCount = 5,
        price = 30000
    )

    val vehicleResponse = vehicle.toResponse()

    val vehicleRequest = VehicleRequest(
        title = "test request",
        manufacturer = "test request manufacturer",
        model = "test request model",
        distance = 200,
        firstRegistration = 2021,
        fuelType = "Diesel",
        horsePower = 200,
        kiloWats = 200,
        transmission = "Manual",
        numberOfOwners = 1,
        color = "black",
        doorCount = 5,
        price = 30000
    )

    val seller = Seller(
        id = "test123",
        firstName = "seller test name",
        lastName = "Seller test last",
        userName = "Seller username",
        phoneNumber = "123456789",
        email = "seller@seller.com",
        location = "Bosnia",
        userId = "userid123"
    )

    val sellerResponse = seller.toResponse()

    val sellerRequest = SellerRequest(
        firstName = "test name",
        lastName = "test last",
        userName = "test username",
        phoneNumber = "1234",
        email = "test@test.com",
        location = "test location"
    )

    val sellerFromRequest = Seller(
        id = "test123",
        firstName = "test name",
        lastName = "test last",
        userName = "test username",
        phoneNumber = "1234",
        email = "test@test.com",
        location = "test location",
        userId = "userid123",
        vehicles = mutableListOf()
    )

    val loginRequest = LoginRequest(
        username = "test@test.com",
        password = "test"
    )

    val registrationRequest = RegistrationRequest(
        username = "test@test.com",
        password = "test"
    )

    val user = User(
        email = "test@test.com",
        uPassword = "test",
        roles = setOf(Role.USER)
    )

    val manufacturer = Manufacturer(
        name = "Audi",
        models = mutableSetOf("A1", "A2", "A3", "A4", "A5", "A6", "A7")
    )
}