package com.example.carbud.seller

import com.example.carbud.BaseUnitTest
import com.example.carbud.utils.ObjectMother
import com.example.carbud.seller.exceptions.SellerNotFoundException
import com.example.carbud.vehicle.VehicleService
import com.example.carbud.vehicle.toEntity
import com.example.carbud.vehicle.toVehicleInfo
import io.mockk.*
import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.assertThrows

class SellerServiceTest : BaseUnitTest() {

    private val sellerRepository = mockk<SellerRepository> {
        every { findSellerById("1") } returns ObjectMother.seller()
        every { findSellerById("null-id") } returns null
        every { deleteById("1") } just Runs
        every { save(ObjectMother.sellerFromRequest()) } returns ObjectMother.sellerFromRequest()
    }
    private val vehicleService = mockk<VehicleService> {
        every { createVehicle(ObjectMother.vehicleRequest()) } returns ObjectMother.vehicleRequest().toEntity()
    }

    private val sellerService = SellerService(sellerRepository, vehicleService)

    @Test
    fun `getSellerById when given sellerId return SellerResponse`() {
        val expected = ObjectMother.seller()
        val result = sellerService.getSellerById("1")
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `getSellerById when given sellerId and no seller found throws SellerNotFoundException`() {
        assertThrows<SellerNotFoundException> {
            sellerService.getSellerById("null-id")
        }
    }

    @Test
    fun `createVehicleForSeller when given sellerId and vehicle request creates vehicle and adds it to seller and returns seller`() {
        val vehicleRequest = ObjectMother.vehicleRequest()
        val sellerSlot = slot<Seller>()
        every { sellerRepository.save(capture(sellerSlot)) } returns mockk()

        sellerService.createVehicleForSeller("1", vehicleRequest)

        val capturedVehicle = sellerSlot.captured.vehicles
        assertTrue(capturedVehicle.contains(vehicleRequest.toEntity().toVehicleInfo()))
    }

    @Test
    fun `deleteSellerById when given sellerId deletes seller`() {
        sellerService.deleteSellerById("1")
        verify { sellerRepository.deleteById("1") }
    }

    @Test
    fun `updateSeller when given sellerId and sellerRequest but seller does not exist throw SellerNotFoundException`() {
        val sellerRequest = ObjectMother.sellerRequest()
        assertThrows<SellerNotFoundException> {
            sellerService.updateSeller("null-id", sellerRequest)
        }
    }

    @Test
    fun `updateSeller when given sellerId and sellerRequest persist updated seller`() {
        val sellerRequest = ObjectMother.sellerRequest()
        val seller = ObjectMother.seller().copy(vehicles = mutableListOf())
        every { sellerRepository.findSellerById("2") } returns seller

        val expected = ObjectMother.sellerFromRequest()
        val result = sellerService.updateSeller("2", sellerRequest)

        assertThat(result).isEqualTo(expected)
        verify { sellerRepository.save(any()) }
    }
}