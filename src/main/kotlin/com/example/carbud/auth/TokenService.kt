package com.example.carbud.auth

import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.temporal.ChronoUnit

@Service
class TokenService(private val jwtEncoder: JwtEncoder) {

    fun generateToken(user: User): String {
        val now = Instant.now()
        val roles = user.authorities.map { it.authority }
        val claims = JwtClaimsSet.builder()
            .issuer("carbud")
            .issuedAt(now)
            .expiresAt(now.plus(5, ChronoUnit.MINUTES))
            .subject(user.username)
            .claim("roles", roles)
            .build()

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).tokenValue
    }
}
