package com.example.carbud.seller

import com.example.carbud.seller.dto.SellerRequest
import com.example.carbud.seller.exceptions.SellerAssignedToUserException
import com.example.carbud.seller.exceptions.SellerNotFoundException
import com.example.carbud.vehicle.Vehicle
import com.example.carbud.vehicle.toVehicleInfo
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class SellerService(
    private val sellerRepository: SellerRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

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

    fun createSeller(userId: String, sellerRequest: SellerRequest): Seller {
        return when (val existingSeller = sellerRepository.findSellerByUserId(userId)) {
            null -> sellerRepository.save(sellerRequest.toEntity(userId = userId))
            else -> {
                logger.info("User with id $userId has seller with id ${existingSeller.id} assigned to account")
                throw SellerAssignedToUserException("Seller for this user already exists")
            }
        }
    }

    fun addVehicleToSeller(sellerId: String, vehicle: Vehicle): Seller {
        val seller = getSellerById(sellerId)
        seller.vehicles.add(vehicle.toVehicleInfo())
        return sellerRepository.save(seller)
    }

    fun removeVehicleFromSeller(sellerId: String, vehicle: Vehicle): Seller {
        val seller = getSellerById(sellerId)
        seller.vehicles.remove(vehicle.toVehicleInfo())
        return sellerRepository.save(seller)
    }

    fun deleteSellerById(sellerId: String) {
        sellerRepository.deleteById(sellerId)
    }
}
