package com.example.carbud.auth

import com.example.carbud.auth.dto.ChangePasswordRequest
import com.example.carbud.auth.enums.Role
import com.example.carbud.auth.exceptions.IncorrectOldPasswordException
import com.example.carbud.auth.exceptions.UserNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {
    fun updatePassword(userId: String, request: ChangePasswordRequest) {
        val user = userRepository.findUserById(userId)
            ?: throw UserNotFoundException("User with id: $userId not found")

        if (!passwordEncoder.matches(request.oldPassword, user.password)) {
            throw IncorrectOldPasswordException("Provided old password is incorrect")
        }

        userRepository.save(
            user.copy(uPassword = passwordEncoder.encode(request.newPassword))
        )
    }

    fun updateRoleForUser(userId: String, roles: List<Role>) {
        val user = userRepository.findUserById(userId)
            ?: throw UserNotFoundException("User with id: $userId not found")

        userRepository.save(
            user.copy(roles = roles.toSet())
        )
    }
}