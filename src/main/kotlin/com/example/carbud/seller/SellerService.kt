package com.example.carbud.seller

import com.example.carbud.seller.dto.SellerRequest
import com.example.carbud.seller.exceptions.SellerNotFoundException
import org.springframework.stereotype.Service

@Service
class SellerService(
    private val sellerRepository: SellerRepository
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

    fun deleteSellerById(sellerId: String) = sellerRepository.deleteById(sellerId)
}
