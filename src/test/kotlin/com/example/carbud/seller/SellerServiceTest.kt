package com.example.carbud.seller

import com.example.carbud.BaseUnitTest
import com.example.carbud.seller.exceptions.SellerAssignedToUserException
import com.example.carbud.utils.ObjectMother
import com.example.carbud.seller.exceptions.SellerNotFoundException
import com.example.carbud.vehicle.toVehicleInfo
import io.mockk.*
import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows

class SellerServiceTest : BaseUnitTest() {

    private val sellerRepository = mockk<SellerRepository> {
        every { findSellerById("1") } returns ObjectMother.seller()
        every { findSellerById("null-id") } returns null
        every { deleteById("1") } just Runs
        every { save(ObjectMother.sellerFromRequest()) } returns ObjectMother.sellerFromRequest()
    }

    private val sellerService = SellerService(sellerRepository)

    @Test
    fun `getSellerById when given sellerId return SellerResponse`() {
        val seller = ObjectMother.seller()
        every { sellerRepository.findSellerById("1") } returns seller

        val result = sellerService.getSellerById("1")
        assertThat(result).isEqualTo(seller)
    }

    @Test
    fun `getSellerById when given sellerId and no seller found throws SellerNotFoundException`() {
        every { sellerRepository.findSellerById("null-id") } returns null

        assertThrows<SellerNotFoundException> {
            sellerService.getSellerById("null-id")
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

    @Test
    fun `updateSeller when given sellerId and sellerRequest but seller does not exist throw SellerNotFoundException`() {
        val sellerRequest = ObjectMother.sellerRequest()
        assertThrows<SellerNotFoundException> {
            sellerService.updateSeller("null-id", sellerRequest)
        }
    }

    @Test
    fun `createSeller when given non-assigned userId and sellerRequest return Seller`() {
        val seller = ObjectMother.sellerRequest().toEntity("1")

        every { sellerRepository.findSellerByUserId("1")} returns null
        every { sellerRepository.save(seller) } returns seller

        val result = sellerService.createSeller("1", ObjectMother.sellerRequest())
        assertEquals(seller, result)
    }

    @Test
    fun `createSeller when given userId and sellerRequest and user assigned to seller throws SellerAssignedToUserException`() {
        val seller = ObjectMother.seller()
        val request = ObjectMother.sellerRequest()

        every { sellerRepository.findSellerByUserId("0")} returns seller

        assertThrows<SellerAssignedToUserException> {
            sellerService.createSeller("0", request)
        }
    }

    @Test
    fun `addVehicleToSeller when given sellerId and vehicle persists vehicle to seller vehicle list`() {
        val seller = ObjectMother.seller()
        val vehicle = ObjectMother.vehicle()

        every { sellerRepository.findSellerById("1") } returns seller
        every { sellerRepository.save(any()) } returns mockk()

        sellerService.addVehicleToSeller("1", vehicle)

        assert(seller.vehicles.contains(vehicle.toVehicleInfo()))
        verify { sellerRepository.save(seller) }
    }

    @Test
    fun `updateVehicleForSeller when given sellerId and vehicle updates vehicle associated to seller`() {
        val vehicle = ObjectMother.vehicle()
        val seller = ObjectMother.seller().copy(vehicles = mutableListOf(vehicle.toVehicleInfo()))

        every { sellerRepository.findSellerById("1") } returns seller
        every { sellerRepository.save(any()) } returns seller

        val updatedVehicle = vehicle.copy(title = "modified test title")
        val result = sellerService.updateVehicleForSeller("1", updatedVehicle)

        assert(result.vehicles.contains(updatedVehicle.toVehicleInfo()))
        verify { sellerRepository.save(result) }
    }

    @Test
    fun `removeVehicleFromSeller when given sellerId and vehicle removes vehicle from seller vehicle list`() {
        val vehicle = ObjectMother.vehicle()
        val seller = ObjectMother.seller().copy(vehicles = mutableListOf(vehicle.toVehicleInfo()))

        every { sellerRepository.findSellerById("1") } returns seller
        every { sellerRepository.save(any()) } returns seller.copy(vehicles = mutableListOf())

        val result = sellerService.removeVehicleFromSeller("1", vehicle)

        assert(!seller.vehicles.contains(vehicle.toVehicleInfo()))
        verify { sellerRepository.save(result) }
    }

    @Test
    fun `deleteSellerById when given sellerId deletes seller`() {
        sellerService.deleteSellerById("1")
        verify { sellerRepository.deleteById("1") }
    }
}