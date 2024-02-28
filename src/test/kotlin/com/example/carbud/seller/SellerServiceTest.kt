package com.example.carbud.seller

import com.example.carbud.BaseUnitTest
import com.example.carbud.utils.ObjectMother
import com.example.carbud.seller.exceptions.SellerNotFoundException
import io.mockk.*
import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import java.util.Optional

class SellerServiceTest : BaseUnitTest() {

    private val sellerRepository = mockk<SellerRepository> {
        every { findById("1") } returns ObjectMother.sellerOptional
        every { findById("null-id") } returns Optional.empty()
        every { deleteById("1") } just Runs
        every { save(ObjectMother.sellerFromRequest) } returns ObjectMother.sellerFromRequest
    }

    private val sellerService = SellerService(sellerRepository)

    private val sellerRequest = ObjectMother.sellerRequest

    @Test
    fun `getSellerById when given sellerId return SellerResponse`() {
        val expected = ObjectMother.sellerResponse
        val result = sellerService.getSellerById("1")
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `getSellerById when given sellerId and no seller found return null`() {
        val result = sellerService.getSellerById("null-id")
        assertThat(result).isNull()
    }

    @Test
    fun `deleteSellerById when given sellerId deletes seller`() {
        sellerService.deleteSellerById("1")
        verify { sellerRepository.deleteById("1") }
    }

    @Test
    fun `updateSeller when given sellerId and sellerRequest but seller does not exist throw SellerNotFoundException`() {
        assertThatExceptionOfType(SellerNotFoundException::class.java)
            .isThrownBy { sellerService.updateSeller("null-id", sellerRequest) }
    }

    @Test
    fun `updateSeller when given sellerId and sellerRequest persist updated seller`() {
        val expected = ObjectMother.sellerResponseFromRequest
        val result = sellerService.updateSeller("1", sellerRequest)
        assertThat(result).isEqualTo(expected)
        verify { sellerRepository.save(any()) }
    }
}