package com.example.carbud.auth

import com.example.carbud.auth.dto.ChangePasswordRequest
import com.example.carbud.auth.dto.LoginRequest
import com.example.carbud.auth.dto.RegistrationRequest
import com.example.carbud.auth.exceptions.UserMissingClaimException
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin
class AuthController(
    private val authService: AuthService,
) {
    @PostMapping("/login")
    fun login(@RequestBody loginRequest: LoginRequest) = authService.login(loginRequest)

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    fun register(@RequestBody registrationRequest: RegistrationRequest) = authService.register(registrationRequest)

    // TODO: check exceptions for change password, invalidate tokens with redis, init script
}
