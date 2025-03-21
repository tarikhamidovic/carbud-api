package com.example.carbud.vehicle

import com.example.carbud.auth.SecurityService
import com.example.carbud.auth.exceptions.ActionNotAllowedException
import com.example.carbud.auth.exceptions.UserMissingClaimException
import com.example.carbud.manufacturer.ManufacturerService
import com.example.carbud.seller.SellerService
import com.example.carbud.vehicle.dto.VehicleRequest
import com.example.carbud.vehicle.enums.FuelType
import com.example.carbud.vehicle.enums.Transmission
import com.example.carbud.vehicle.exceptions.VehicleNotFoundException
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class VehicleService(
    private val vehicleRepository: VehicleRepository,
    private val manufacturerService: ManufacturerService,
    private val sellerService: SellerService,
    private val mongoTemplate: MongoTemplate,
    private val securityService: SecurityService
) {
    companion object {
        private const val PAGE = "page"
        private const val MANUFACTURER = "manufacturer"
        private const val MODEL = "model"
        private const val DISTANCE = "distance"
        private const val DISTANCE_LT = "distancelt"
        private const val FIRST_REGISTRATION = "firstRegistration"
        private const val FUEL_TYPE = "fuelType"
        private const val TRANSMISSION = "transmission"
        private const val COLOR = "color"
        private const val PRICE = "price"
        private const val PRICE_LT = "pricelt"
    }

    fun getFilteredVehicles(params: Map<String, String>): Page<Vehicle> {
        val page = PageRequest.of(
            params[PAGE]?.toIntOrNull() ?: 0,
            10
        )
        val query = Query().with(page)
        params[MANUFACTURER]?.let {
            query.addCriteria(Criteria.where(MANUFACTURER).`is`(it))
        }
        params[MODEL]?.let {
            query.addCriteria(Criteria.where(MODEL).`is`(it))
        }
        params[FUEL_TYPE]?.let {
            query.addCriteria(Criteria.where(FUEL_TYPE).`is`(FuelType.valueOf(it.uppercase())))
        }
        params[TRANSMISSION]?.let {
            query.addCriteria(Criteria.where(TRANSMISSION).`is`(Transmission.valueOf(it.uppercase())))
        }
        params[COLOR]?.let {
            query.addCriteria(Criteria.where(COLOR).`is`(it))
        }
        params[DISTANCE_LT]?.let { distance ->
            distance.toIntOrNull()?.let {
                query.addCriteria(Criteria.where(DISTANCE).lte(it))
            }
        }
        params[FIRST_REGISTRATION]?.let { firstRegistration ->
            firstRegistration.toIntOrNull()?.let {
                query.addCriteria(Criteria.where(FIRST_REGISTRATION).gte(it))
            }
        }
        params[PRICE_LT]?.let { price ->
            price.toIntOrNull()?.let {
                query.addCriteria(Criteria.where(PRICE).lte(it))
            }
        }
        val countQuery = Query.of(query)
        val totalCount = mongoTemplate.count(countQuery, Vehicle::class.java)
        val result = mongoTemplate.find(query, Vehicle::class.java)

        return PageImpl(result, page, totalCount)
    }

    fun getVehicleById(vehicleId: String): Vehicle {
        return vehicleRepository.findVehicleById(vehicleId)
            ?: throw VehicleNotFoundException("Vehicle with provided id: $vehicleId does not exist")
    }

    @Transactional
    fun createVehicle(sellerId: String, vehicleRequest: VehicleRequest): Vehicle {
        val vehicle = vehicleRepository.save(vehicleRequest.toEntity(sellerId = sellerId))
        manufacturerService.addModelToManufacturer(vehicleRequest.manufacturer, vehicleRequest.model)
        sellerService.addVehicleToSeller(sellerId, vehicle)

        return vehicle
    }

    @Transactional
    fun updateVehicle(vehicleId: String, vehicleRequest: VehicleRequest): Vehicle {
        val existingVehicle = getVehicleById(vehicleId)

        val sellerId = securityService.sellerId
            ?: throw UserMissingClaimException("Security context missing claim sellerId")

        if (existingVehicle.sellerId != sellerId) {
            if (!securityService.isAdmin()) {
                throw ActionNotAllowedException("Seller with id: $sellerId not authorized to edit vehicle with id: ${existingVehicle.id}")
            }
        }

        val updatedVehicle = existingVehicle.copy(
            title = vehicleRequest.title,
            manufacturer = vehicleRequest.manufacturer,
            model = vehicleRequest.model,
            distance = vehicleRequest.distance,
            firstRegistration = vehicleRequest.firstRegistration,
            fuelType = FuelType.valueOf(vehicleRequest.fuelType.uppercase()),
            horsePower = vehicleRequest.horsePower,
            kiloWats = vehicleRequest.kiloWats,
            transmission = Transmission.valueOf(vehicleRequest.transmission.uppercase()),
            numberOfOwners = vehicleRequest.numberOfOwners,
            color = vehicleRequest.color,
            doorCount = vehicleRequest.doorCount,
            price = vehicleRequest.price
        )
        sellerService.updateVehicleForSeller(sellerId, updatedVehicle)
        return vehicleRepository.save(updatedVehicle)
    }

    fun deleteVehiclesBySellerId(sellerId: String) = vehicleRepository.deleteVehicleBySellerId(sellerId)

    @Transactional
    fun deleteVehicleById(vehicleId: String) {
        val vehicle = getVehicleById(vehicleId)

        val sellerId = securityService.sellerId
            ?: throw UserMissingClaimException("Security context missing claim sellerId")

        if (vehicle.sellerId != sellerId) {
            if (!securityService.isAdmin()) {
                throw ActionNotAllowedException("User not authorized to delete vehicle with id: ${vehicle.id}")
            }
        }

        sellerService.removeVehicleFromSeller(vehicle.sellerId, vehicle)
        vehicleRepository.deleteById(vehicleId)
    }
}