package com.example.carbud.auth

import com.example.carbud.BaseUnitTest
import com.example.carbud.auth.dto.ChangePasswordRequest
import com.example.carbud.auth.enums.Role
import com.example.carbud.auth.exceptions.IncorrectOldPasswordException
import com.example.carbud.auth.exceptions.UserNotFoundException
import com.example.carbud.utils.ObjectMother
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.Test
import org.springframework.security.crypto.password.PasswordEncoder

class UserServiceTest : BaseUnitTest() {

    private val userRepository = mockk<UserRepository>()
    private val passwordEncoder = mockk<PasswordEncoder>()

    private val userService = UserService(userRepository, passwordEncoder)

    private val user = ObjectMother.user().copy(userName = "test-user")

    @Test
    fun `updatePassword when given userId but user does not exist throw UserNotFoundException`() {
        every { userRepository.findUserById("null-id") } returns null

        val request = ChangePasswordRequest(
            oldPassword = "oldPassword",
            newPassword = "newPassword"
        )

        assertThatExceptionOfType(UserNotFoundException::class.java)
            .isThrownBy { userService.updatePassword("null-id", request) }
    }

    @Test
    fun `updatePassword when given userId and changePasswordRequest but old password does not match throw IncorrectOldPasswordException`() {
        every { userRepository.findUserById("test-user") } returns user
        every { passwordEncoder.matches("oldPassword", user.password) } returns false

        val request = ChangePasswordRequest(
            oldPassword = "oldPassword",
            newPassword = "newPassword"
        )

        assertThatExceptionOfType(IncorrectOldPasswordException::class.java)
            .isThrownBy { userService.updatePassword("test-user", request) }
    }

    @Test
    fun `updatePassword when given userId and changePasswordRequest saves new password`() {
        every { userRepository.findUserById("test-user") } returns user
        every { passwordEncoder.matches("test", user.password) } returns true
        every { passwordEncoder.encode("newPassword") } returns "newPassword"
        every { userRepository.save(any()) } returns mockk()

        val request = ChangePasswordRequest(
            oldPassword = "test",
            newPassword = "newPassword"
        )

        userService.updatePassword("test-user", request)
        verify { userRepository.save(user.copy(uPassword = "newPassword")) }
    }

    @Test
    fun `updateRoleForUser when given userId but user does not exist throws UserNotFoundException`() {
        every { userRepository.findUserById("test-user") } returns null

        assertThatExceptionOfType(UserNotFoundException::class.java)
            .isThrownBy { userService.updateRoleForUser("test-user", listOf(Role.ADMIN)) }
    }

    @Test
    fun `updateRoleForUser when given userId and role list assigns roles to user`() {
        every { userRepository.findUserById("test-user") } returns user
        every { userRepository.save(any()) } returns mockk()

        val roles = listOf(Role.ADMIN, Role.USER)
        userService.updateRoleForUser("test-user", roles)

        verify { userRepository.save(user.copy(roles = roles.toSet())) }
    }
}