package com.example.carbud.manufacturer

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface ManufacturerRepository : MongoRepository<Manufacturer, String> {
    fun findManufacturerByName(name: String): Manufacturer?
}
