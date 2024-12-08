package com.example.carbud.vehicle

import com.example.carbud.BaseUnitTest
import com.example.carbud.manufacturer.ManufacturerService
import com.example.carbud.seller.SellerService
import com.example.carbud.utils.ObjectMother
import com.example.carbud.vehicle.exceptions.VehicleNotFoundException
import io.mockk.*
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.mongodb.core.MongoTemplate

class VehicleServiceTest : BaseUnitTest() {

    private val vehicleRepository = mockk<VehicleRepository> {
        every { findVehicleById("abc123") } returns ObjectMother.vehicle()
        every { findVehicleById("no123") } returns null
        every { save(any()) } returns ObjectMother.vehicle()
        every { deleteById(any()) } just Runs
    }
    private val manufacturerService = mockk<ManufacturerService> {
        every { addModelToManufacturer(any(), any()) } just Runs
    }
    private val sellerService = mockk<SellerService> {}
    private val mongoTemplate = mockk<MongoTemplate> {
        every { count(any(), Vehicle::class.java) } returns 1
        every { find(any(), Vehicle::class.java) } returns listOf(ObjectMother.vehicle())
    }

    private val vehicleService = VehicleService(vehicleRepository, manufacturerService, sellerService, mongoTemplate)

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

        every { sellerService.addVehicleToSeller(expected.sellerId!!, expected) }

        val result = vehicleService.createVehicle(expected.sellerId!!, ObjectMother.vehicleRequest())
        assertThat(result).isEqualTo(expected)
        verify(exactly = 1) { vehicleRepository.save(any()) }
    }

    @Test
    fun `updateVehicle when given existing Vehicle update and persist`() {
        val expected = ObjectMother.vehicle()
        val result = vehicleService.updateVehicle("abc123", ObjectMother.vehicleRequest())
        assertThat(result).isEqualTo(expected)
        verify(exactly = 1) { vehicleRepository.save(any()) }
    }

    @Test
    fun `updateVehicle when given Vehicle which does not exist throw VehicleNotFoundException`() {
        assertThrows<VehicleNotFoundException> {
            vehicleService.updateVehicle("no123", ObjectMother.vehicleRequest())
        }
    }

    @Test
    fun deleteVehicleById() {
        vehicleService.deleteVehicleById("abc123")
        verify(exactly = 1) { vehicleRepository.deleteById("abc123") }
    }
}