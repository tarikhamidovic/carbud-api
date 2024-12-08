package com.example.carbud.auth

import com.example.carbud.BaseUnitTest
import com.example.carbud.auth.dto.LoginRequest
import com.example.carbud.auth.dto.RegistrationRequest
import com.example.carbud.auth.exceptions.UserAlreadyExistsException
import com.example.carbud.auth.exceptions.UserNotFoundException
import com.example.carbud.utils.ObjectMother
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.Test
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.crypto.password.PasswordEncoder

class AuthServiceTest : BaseUnitTest() {

    private val user = ObjectMother.user()

    private val userRepository = mockk<UserRepository> {
        every { findUserByUserName("test@test.com") } returns null
        every { save(user) } returns user
    }
    private val tokenService = mockk<TokenService> {
        every { generateToken(user) } returns "token"
    }
    private val passwordEncoder = mockk<PasswordEncoder> {
        every { encode("test") } returns "test"
    }
    private val authenticationManager = mockk<AuthenticationManager> {
        every { authenticate(any()) } returns mockk()
    }

    private val authService = AuthService(userRepository, tokenService, passwordEncoder, authenticationManager)

    @Test
    fun `register when given registration request saves user and generates token`() {
        val result = authService.register(RegistrationRequest(username = user.username, password = user.password))
        assertThat(result).isEqualTo("token")
        verify { userRepository.save(user) }
    }

    @Test
    fun `register when given registration request and user with email already exists throw UserAlreadyExistsException`() {
        every { userRepository.findUserByUserName(user.username) } returns user
        val request = RegistrationRequest(username = user.username, password = user.password)

        assertThatExceptionOfType(UserAlreadyExistsException::class.java)
            .isThrownBy { authService.register(request) }
    }

    @Test
    fun `login when given login request generate token`() {
        every { userRepository.findUserByUserName(user.username) } returns user
        val result = authService.login(LoginRequest(username = user.username, password = user.password))
        assertThat(result).isEqualTo("token")
    }

    @Test
    fun `login when given login request and user not exists throw UserNotFoundException`() {
        val request = LoginRequest(username = user.username, password = user.password)

        assertThatExceptionOfType(UserNotFoundException::class.java)
            .isThrownBy { authService.login(request) }
    }
}