package com.example.carbud.auth

import com.example.carbud.seller.SellerRepository
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.temporal.ChronoUnit

@Service
class TokenService(
    private val jwtEncoder: JwtEncoder,
    private val sellerRepository: SellerRepository
) {

    companion object {
        private const val USER_ID = "user_id"
        private const val ROLES = "roles"
        private const val SELLER_ID = "seller_id"
    }

    fun generateToken(user: User): String {
        val now = Instant.now()
        val roles = user.authorities.map { it.authority }

        val claimsBuilder = JwtClaimsSet.builder()
            .issuer("carbud")
            .issuedAt(now)
            .expiresAt(now.plus(5, ChronoUnit.MINUTES))
            .subject(user.username)
            .claim(USER_ID, user.id)
            .claim(ROLES, roles)

        user.id?.let {
            val sellerId = sellerRepository.findSellerByUserId(it)?.id
            if (sellerId != null) {
                claimsBuilder.claim(SELLER_ID, sellerId)
            }
        }
        val claims = JwtEncoderParameters.from(claimsBuilder.build())
        return jwtEncoder.encode(claims).tokenValue
    }
}
