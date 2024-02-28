package com.example.carbud.auth

import com.example.carbud.auth.dto.LoginRequest
import com.example.carbud.auth.dto.RegistrationRequest
import com.example.carbud.auth.enums.Role
import com.example.carbud.auth.exceptions.UserAlreadyExistsException
import com.example.carbud.auth.exceptions.UserNotFoundException
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
    private val passwordEncoder: PasswordEncoder,
    private val authenticationManager: AuthenticationManager
) {

    fun register(request: RegistrationRequest): String {
        val existingUser = userRepository.findUserByEmail(request.username)

        if (existingUser != null) throw UserAlreadyExistsException(
            "User with email: ${request.username} already exists"
        )

        val user = userRepository.save(
            User(
                email = request.username,
                uPassword = passwordEncoder.encode(request.password),
                roles = setOf(Role.USER)
            )
        )
        return tokenService.generateToken(user)
    }

    fun login(request: LoginRequest): String {
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(request.username, request.password)
        )
        val user = userRepository.findUserByEmail(request.username)
            ?: throw UserNotFoundException("User with email: ${request.username} not found")

        return tokenService.generateToken(user)
    }
}