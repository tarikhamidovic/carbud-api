package com.example.carbud.auth

import com.example.carbud.BaseUnitTest
import com.example.carbud.seller.SellerRepository
import com.example.carbud.utils.ObjectMother
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters

class TokenServiceTest : BaseUnitTest() {

    private val jwtEncoder = mockk<JwtEncoder>()

    private val sellerRepository = mockk<SellerRepository>()

    private val tokenService = TokenService(jwtEncoder, sellerRepository)

    @Test
    fun `generateToken when given user and seller assigned to user returns jwt with sellerId claim`() {
        val user = ObjectMother.registeredUser()
        val seller = ObjectMother.seller()
        val jwt = mockk<Jwt>()
        val claimsSlot = slot<JwtEncoderParameters>()

        every { sellerRepository.findSellerByUserId("userid123") } returns seller
        every { jwtEncoder.encode(capture(claimsSlot)) } returns jwt
        every { jwt.tokenValue } returns "token"

        tokenService.generateToken(user)

        assertEquals(seller.id, claimsSlot.captured.claims.claims["seller_id"])
    }

    @Test
    fun `generateToken when given user and seller not assigned to user returns jwt without sellerId claim`() {
        val user = ObjectMother.registeredUser()
        val jwt = mockk<Jwt>()
        val claimsSlot = slot<JwtEncoderParameters>()

        every { sellerRepository.findSellerByUserId("userid123") } returns null
        every { jwtEncoder.encode(capture(claimsSlot)) } returns jwt
        every { jwt.tokenValue } returns "token"

        tokenService.generateToken(user)

        assertNull(claimsSlot.captured.claims.claims["seller_id"])
    }
}