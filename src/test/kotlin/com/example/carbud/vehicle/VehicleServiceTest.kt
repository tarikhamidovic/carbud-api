package com.example.carbud.vehicle

import com.example.carbud.BaseUnitTest
import com.example.carbud.utils.ObjectMother
import com.example.carbud.vehicle.exceptions.VehicleNotFoundException
import io.mockk.*
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.mongodb.core.MongoTemplate
import java.util.Optional

class VehicleServiceTest : BaseUnitTest() {

    private val vehicleOptional = Optional.of(ObjectMother.vehicle)

    private val vehicleRepository = mockk<VehicleRepository>(){
        every { findById("abc123") } returns vehicleOptional
        every { findById("no123") } returns Optional.empty()
        every { save(any()) } returns ObjectMother.vehicle
        every { deleteById(any()) } just Runs
    }
    private val mongoTemplate = mockk<MongoTemplate>() {
        every { count(any(), Vehicle::class.java) } returns 1
        every { find(any(), Vehicle::class.java) } returns listOf(ObjectMother.vehicle)
    }

    private val vehicleService = VehicleService(vehicleRepository, mongoTemplate)

    @Test
    fun `getFilteredVehicles when page number not given should return page zero with VehicleResponse`() {
        val expected = PageImpl(
            listOf(ObjectMother.vehicleResponse),
            PageRequest.of(0, 10),
            1
        )
        val result = vehicleService.getFilteredVehicles(mapOf("page" to "0"))
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `getFilteredVehicles when given filters should return page with VehicleResponse`() {
        val expected = PageImpl(
            listOf(ObjectMother.vehicleResponse),
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
        val expected = ObjectMother.vehicleResponse
        val result = vehicleService.getVehicleById("abc123")
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `getVehicleById when given vehicleId and no vehicle found return null`() {
        val result = vehicleService.getVehicleById("no123")
        assertThat(result).isNull()
    }

    @Test
    fun `createVehicle when given VehicleRequest persist new Vehicle`() {
        val expected = ObjectMother.vehicle
        val result = vehicleService.createVehicle(ObjectMother.vehicleRequest)
        assertThat(result).isEqualTo(expected)
        verify(exactly = 1) { vehicleRepository.save(any()) }
    }

    @Test
    fun `updateVehicle when given existing Vehicle update and persist`() {
        val expected = ObjectMother.vehicleResponse
        val result = vehicleService.updateVehicle("abc123", ObjectMother.vehicleRequest)
        assertThat(result).isEqualTo(expected)
        verify(exactly = 1) { vehicleRepository.save(any()) }
    }

    @Test
    fun `updateVehicle when given Vehicle which does not exist throw VehicleNotFoundException`() {
        assertThatExceptionOfType(VehicleNotFoundException::class.java)
            .isThrownBy { vehicleService.updateVehicle("no123", ObjectMother.vehicleRequest) }
    }

    @Test
    fun deleteVehicleById() {
        vehicleService.deleteVehicleById("abc123")
        verify(exactly = 1) { vehicleRepository.deleteById("abc123") }
    }
}