package com.example.carbud.auth

import com.example.carbud.auth.enums.Role
import com.example.carbud.auth.exceptions.ActionNotAllowedException
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service

@Service
class SecurityService {

    companion object {
        private const val USER_ID = "user_id"
        private const val SELLER_ID = "seller_id"
        private const val ROLES = "roles"
    }

    private val authentication: Authentication
        get() = SecurityContextHolder.getContext().authentication

    private val principal: Any
        get() = authentication.principal

    private val jwt: Jwt?
        get() = if (principal is Jwt) principal as Jwt else null

    val claims: Map<String, Any>?
        get() = jwt?.claims

    val sellerId: String?
        get() = claims?.get(SELLER_ID).toString()

    val userId: String?
        get() = claims?.get(USER_ID).toString()

    val roles: List<String>
        get() = claims?.get(ROLES) as List<String>

    fun isAdmin() = roles.contains(Role.ADMIN.name)

    fun isAuthenticatedWithUserId(userId: String) = this.userId == userId
}