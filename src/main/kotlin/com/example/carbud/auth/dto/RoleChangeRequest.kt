package com.example.carbud.auth.dto

import com.example.carbud.auth.enums.Role

data class RoleChangeRequest(
    val roles: List<Role>
)
