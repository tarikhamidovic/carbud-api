package com.example.carbud.auth

import com.example.carbud.auth.dto.LoginRequest
import com.example.carbud.auth.dto.RegistrationRequest
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
@CrossOrigin
class AuthController(
    private val authService: AuthService
) {
    @PostMapping("/login")
    fun login(@RequestBody loginRequest: LoginRequest) = authService.login(loginRequest)

    @PostMapping("/register")
    fun register(@RequestBody registrationRequest: RegistrationRequest) = authService.register(registrationRequest)
}
