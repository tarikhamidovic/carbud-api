package com.example.carbud.vehicle

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface VehicleRepository : MongoRepository<Vehicle, String>
