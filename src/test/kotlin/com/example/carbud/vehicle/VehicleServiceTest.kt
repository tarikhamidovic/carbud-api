package com.example.carbud.vehicle

import com.example.carbud.BaseUnitTest
import com.example.carbud.auth.SecurityService
import com.example.carbud.auth.exceptions.ActionNotAllowedException
import com.example.carbud.auth.exceptions.UserMissingClaimException
import com.example.carbud.manufacturer.ManufacturerService
import com.example.carbud.seller.SellerService
import com.example.carbud.utils.ObjectMother
import com.example.carbud.vehicle.enums.FuelType
import com.example.carbud.vehicle.enums.Transmission
import com.example.carbud.vehicle.exceptions.VehicleNotFoundException
import io.mockk.*
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.mongodb.core.MongoTemplate

class VehicleServiceTest : BaseUnitTest() {

    private val vehicleRepository = mockk<VehicleRepository>(relaxed = true) {
        every { findVehicleById("abc123") } returns ObjectMother.vehicle()
        every { findVehicleById("no123") } returns null
        every { save(any()) } returns ObjectMother.vehicle()
        every { deleteById(any()) } just Runs
    }
    private val manufacturerService = mockk<ManufacturerService> {
        every { addModelToManufacturer(any(), any()) } just Runs
    }
    private val sellerService = mockk<SellerService>(relaxed = true)
    private val mongoTemplate = mockk<MongoTemplate> {
        every { count(any(), Vehicle::class.java) } returns 1
        every { find(any(), Vehicle::class.java) } returns listOf(ObjectMother.vehicle())
    }
    private val securityService = mockk<SecurityService>()

    private val vehicleService = VehicleService(
        vehicleRepository,
        manufacturerService,
        sellerService,
        mongoTemplate,
        securityService
    )

    @Test
    fun `getFilteredVehicles when page number not given should return page zero with Vehicle`() {
        val expected = PageImpl(
            listOf(ObjectMother.vehicle()),
            PageRequest.of(0, 10),
            1
        )
        val result = vehicleService.getFilteredVehicles(mapOf("page" to "0"))
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `getFilteredVehicles when given filters should return Vehicle page`() {
        val expected = PageImpl(
            listOf(ObjectMother.vehicle()),
            PageRequest.of(0, 10),
            1
        )
        val result = vehicleService.getFilteredVehicles(
            mapOf(
                "page" to "0",
                "manufacturer" to "test manufacturer",
                "model" to "test model",
                "fuelType" to "Petrol",
                "transmission" to "Automatic",
                "color" to "black",
                "distance" to "100",
                "firstRegistration" to "2020",
                "price" to "30000"
            )
        )
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `getVehicleById when given vehicleId return Vehicle`() {
        val expected = ObjectMother.vehicle()
        val result = vehicleService.getVehicleById("abc123")
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `getVehicleById when given vehicleId and no vehicle found throws VehicleNotFoundException`() {
        assertThrows<VehicleNotFoundException> {
            vehicleService.getVehicleById("no123")
        }
    }

    @Test
    fun `createVehicle when given VehicleRequest persist new Vehicle`() {
        val expected = ObjectMother.vehicle()
        val request = ObjectMother.vehicleRequest()

        every { vehicleRepository.save(any()) } returns expected
        every { manufacturerService.addModelToManufacturer(any(), any()) } just Runs
        every { sellerService.addVehicleToSeller(any(), any()) } returns mockk()

        val result = vehicleService.createVehicle(expected.sellerId, request)
        assertThat(result).isEqualTo(expected)

        verifyAll {
            vehicleRepository.save(request.toEntity(expected.sellerId))
            manufacturerService.addModelToManufacturer(request.manufacturer, request.model)
            sellerService.addVehicleToSeller(expected.sellerId, expected)
        }
    }

    @Test
    fun `updateVehicle when given Vehicle which does not exist throw VehicleNotFoundException`() {
        assertThrows<VehicleNotFoundException> {
            vehicleService.updateVehicle("no123", ObjectMother.vehicleRequest())
        }
    }

    @Test
    fun `updateVehicle when security context does not contain sellerId claim throws UserMissingClaimException`() {
        every { securityService.sellerId } returns null

        assertThrows<UserMissingClaimException> {
            vehicleService.updateVehicle("000", ObjectMother.vehicleRequest())
        }
    }

    @Test
    fun `updateVehicle when user's sellerId does not match vehicle sellerId nor has admin role throw ActionNotAllowedException`() {
        val vehicle = ObjectMother.vehicle()
        val request = ObjectMother.vehicleRequest()

        every { vehicleRepository.findVehicleById(vehicle.id!!) } returns vehicle
        every { securityService.sellerId } returns "not-1"
        every { securityService.isAdmin() } returns false

        assertThrows<ActionNotAllowedException> {
            vehicleService.updateVehicle(vehicle.id!!, request)
        }
    }

    @Test
    fun `updateVehicle when user's sellerId does not match vehicle sellerId but has admin role does not throw ActionNotAllowedException`() {
        val vehicle = ObjectMother.vehicle()
        val request = ObjectMother.vehicleRequest()

        every { vehicleRepository.findVehicleById(vehicle.id!!) } returns vehicle
        every { securityService.sellerId } returns "not-1"
        every { securityService.isAdmin() } returns true

        assertDoesNotThrow {
            vehicleService.updateVehicle(vehicle.id!!, request)
        }
    }

    @Test
    fun `updateVehicle when given vehicleId and vehicleRequest and existing vehicle update and persist`() {
        val vehicle = ObjectMother.vehicle()
        val request = ObjectMother.vehicleRequest()

        every { vehicleRepository.findVehicleById(vehicle.id!!) } returns vehicle
        every { securityService.sellerId } returns "1"

        val expected = vehicle.copy(
            title = request.title,
            manufacturer = request.manufacturer,
            model = request.model,
            distance = request.distance,
            firstRegistration = request.firstRegistration,
            fuelType = FuelType.valueOf(request.fuelType.uppercase()),
            horsePower = request.horsePower,
            kiloWats = request.kiloWats,
            transmission = Transmission.valueOf(request.transmission.uppercase()),
            numberOfOwners = request.numberOfOwners,
            color = request.color,
            doorCount = request.doorCount,
            price = request.price
        )
        vehicleService.updateVehicle(vehicle.id!!, request)
        verify { vehicleRepository.save(expected) }
    }

    @Test
    fun `deleteVehiclesBySellerId when given sellerId deletes vehicles`() {
        val sellerId = "abc123"
        vehicleService.deleteVehiclesBySellerId(sellerId)
        verify(exactly = 1) { vehicleRepository.deleteVehicleBySellerId(sellerId) }
    }

    @Test
    fun `deleteVehicleById when security context does not contain sellerId throw UserMissingClaimException`() {
        every { securityService.sellerId } returns null

        assertThrows<UserMissingClaimException> {
            vehicleService.deleteVehicleById("1")
        }
    }

    @Test
    fun `deleteVehicleById when user's sellerId does not match vehicle sellerId nor has admin role throw ActionNotAllowedException`() {
        val vehicle = ObjectMother.vehicle()
        val sellerId = "not-1"

        every { vehicleRepository.findVehicleById(vehicle.id!!) } returns vehicle
        every { securityService.sellerId } returns sellerId
        every { securityService.isAdmin() } returns false

        assertThrows<ActionNotAllowedException> {
            vehicleService.deleteVehicleById(vehicle.id!!)
        }
    }

    @Test
    fun `deleteVehicleById when user's sellerId does not match vehicle sellerId but has admin role does not throw ActionNotAllowedException`() {
        val vehicle = ObjectMother.vehicle()
        val sellerId = "not-1"

        every { vehicleRepository.findVehicleById(vehicle.id!!) } returns vehicle
        every { securityService.sellerId } returns sellerId
        every { securityService.isAdmin() } returns true

        assertDoesNotThrow {
            vehicleService.deleteVehicleById(vehicle.id!!)
        }
    }

    @Test
    fun `deleteVehicleById when given vehicleId removes vehicle from seller and deletes it`() {
        val vehicle = ObjectMother.vehicle()

        every { vehicleRepository.findVehicleById(vehicle.id!!) } returns vehicle
        every { securityService.sellerId } returns "1"

        vehicleService.deleteVehicleById(vehicle.id!!)

        verify { sellerService.removeVehicleFromSeller(vehicle.sellerId, vehicle) }
        verify { vehicleRepository.deleteById(vehicle.id!!) }
    }
}