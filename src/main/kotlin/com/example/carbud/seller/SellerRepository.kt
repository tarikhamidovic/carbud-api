package com.example.carbud.seller

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface SellerRepository : MongoRepository<Seller, String> {
    fun findSellerById(id: String): Seller?
}
