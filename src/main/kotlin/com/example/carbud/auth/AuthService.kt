package com.example.carbud.auth

import com.example.carbud.auth.dto.ChangePasswordRequest
import com.example.carbud.auth.dto.LoginRequest
import com.example.carbud.auth.dto.RegistrationRequest
import com.example.carbud.auth.enums.Role
import com.example.carbud.auth.exceptions.IncorrectOldPasswordException
import com.example.carbud.auth.exceptions.UserAlreadyExistsException
import com.example.carbud.auth.exceptions.UserNotFoundException
import com.example.carbud.seller.SellerService
import com.example.carbud.seller.dto.SellerRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.AuthenticationException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val tokenService: TokenService,
    private val sellerService: SellerService,
    private val passwordEncoder: PasswordEncoder,
    private val authenticationManager: AuthenticationManager
) {
    fun register(request: RegistrationRequest) {
        val existingUser = userRepository.findUserByUserName(request.username)

        if (existingUser != null) throw UserAlreadyExistsException(
            "User with username: ${request.username} already exists"
        )

        val user = userRepository.save(
            User(
                userName = request.username,
                uPassword = passwordEncoder.encode(request.password),
                roles = setOf(Role.USER)
            )
        )

        sellerService.createSeller(
            user.id!!,
            SellerRequest(
                firstName = request.firstName,
                lastName = request.lastName,
                userName = request.username,
                phoneNumber = request.phoneNumber,
                email = request.email,
                location = request.location
            )
        )
    }

    fun login(request: LoginRequest): String {
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(request.username, request.password)
        )
        val user = userRepository.findUserByUserName(request.username)
            ?: throw UserNotFoundException("User with username: ${request.username} not found")

        return tokenService.generateToken(user)
    }
}
