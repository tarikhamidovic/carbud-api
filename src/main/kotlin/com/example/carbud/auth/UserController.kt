package com.example.carbud.auth

import com.example.carbud.auth.dto.ChangePasswordRequest
import com.example.carbud.auth.dto.RoleChangeRequest
import com.example.carbud.auth.exceptions.ActionNotAllowedException
import com.example.carbud.auth.exceptions.UserMissingClaimException
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
class UserController(
    private val userService: UserService,
    private val securityService: SecurityService
) {
    @PostMapping("/change-password")
    fun changePassword(@RequestBody changePasswordRequest: ChangePasswordRequest) {
        val userId = securityService.userId ?: throw UserMissingClaimException("Security context missing claim userId")
        userService.updatePassword(userId, changePasswordRequest)
    }

    @PostMapping("/{userId}/roles")
    fun updateRolesForUser(@PathVariable userId: String, @RequestBody request: RoleChangeRequest) {
        if (!securityService.isAdmin()) { throw ActionNotAllowedException("User is not admin") }
        userService.updateRoleForUser(userId, request.roles)
    }
}