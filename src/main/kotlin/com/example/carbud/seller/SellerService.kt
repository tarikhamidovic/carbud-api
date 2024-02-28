package com.example.carbud.seller

import com.example.carbud.seller.dto.SellerRequest
import com.example.carbud.seller.dto.SellerResponse
import com.example.carbud.seller.exceptions.SellerNotFoundException
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull

@Service
class SellerService(private val sellerRepository: SellerRepository) {

    fun getSellerById(sellerId: String) = sellerRepository.findById(sellerId).map { it.toResponse() }.getOrNull()

    fun deleteSellerById(sellerId: String) = sellerRepository.deleteById(sellerId)

    fun updateSeller(sellerId: String, sellerRequest: SellerRequest): SellerResponse {
        val existingSeller = sellerRepository.findById(sellerId)
            .orElseThrow { SellerNotFoundException("Seller with id $sellerId does not exist") }

        val updatedSeller = existingSeller.copy(
            firstName = sellerRequest.firstName,
            lastName = sellerRequest.lastName,
            userName = sellerRequest.userName,
            phoneNumber = sellerRequest.phoneNumber,
            email = sellerRequest.email,
            location = sellerRequest.location
        )

        return sellerRepository.save(updatedSeller).toResponse()
    }
}