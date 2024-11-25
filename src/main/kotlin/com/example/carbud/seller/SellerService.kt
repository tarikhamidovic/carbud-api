package com.example.carbud.seller

import com.example.carbud.seller.dto.SellerRequest
import com.example.carbud.seller.exceptions.SellerNotFoundException
import com.example.carbud.vehicle.Vehicle
import com.example.carbud.vehicle.VehicleService
import com.example.carbud.vehicle.dto.VehicleRequest
import com.example.carbud.vehicle.toVehicleInfo
import org.springframework.stereotype.Service

@Service
class SellerService(
    private val sellerRepository: SellerRepository,
    private val vehicleService: VehicleService
) {
    fun getSellerById(sellerId: String): Seller {
        return sellerRepository.findSellerById(sellerId)
            ?: throw SellerNotFoundException("Seller with id $sellerId does not exist")
    }

    fun updateSeller(sellerId: String, sellerRequest: SellerRequest): Seller {
        val existingSeller = getSellerById(sellerId)

        val updatedSeller = existingSeller.copy(
            firstName = sellerRequest.firstName,
            lastName = sellerRequest.lastName,
            userName = sellerRequest.userName,
            phoneNumber = sellerRequest.phoneNumber,
            email = sellerRequest.email,
            location = sellerRequest.location
        )

        return sellerRepository.save(updatedSeller)
    }

    fun createVehicleForSeller(sellerId: String, vehicleRequest: VehicleRequest): Seller {
        val seller = getSellerById(sellerId)
        val vehicle = vehicleService.createVehicle(vehicleRequest)

        seller.vehicles.add(vehicle.toVehicleInfo())
        return sellerRepository.save(seller)
    }

    fun deleteSellerById(sellerId: String) = sellerRepository.deleteById(sellerId)
}
