package com.example.carbud.auth

import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service

@Service
class SecurityService {

    private val authentication: Authentication
        get() = SecurityContextHolder.getContext().authentication

    private val principal: Any
        get() = authentication.principal

    private val jwt: Jwt?
        get() = if (principal is Jwt) principal as Jwt else null

    val claims: Map<String, Any>?
        get() = jwt?.claims
}