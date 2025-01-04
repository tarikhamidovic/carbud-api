package com.example.carbud.auth

import com.example.carbud.BaseControllerTest
import com.example.carbud.auth.dto.ChangePasswordRequest
import com.example.carbud.auth.dto.RoleChangeRequest
import com.example.carbud.auth.enums.Role
import com.ninjasquad.springmockk.MockkBean
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@WebMvcTest(UserController::class)
class UserControllerTest : BaseControllerTest() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var userService: UserService

    @MockkBean
    private lateinit var securityService: SecurityService

    @Test
    fun `changePassword when no userId claim in security context returns 403`() {
        val request = ChangePasswordRequest(
            oldPassword = "oldPassword",
            newPassword = "newPassword"
        )
        every { securityService.userId } returns null

        mockMvc.post("/users/change-password") {
            content = objectMapper.writeValueAsString(request)
            contentType = MediaType.APPLICATION_JSON
        }
            .andExpect {
                status { isForbidden() }
            }

        verify(exactly = 0) { userService.updatePassword(any(), any()) }
    }

    @Test
    fun `changePassword when given request returns 200`() {
        val request = ChangePasswordRequest(
            oldPassword = "oldPassword",
            newPassword = "newPassword"
        )
        every { securityService.userId } returns "1"
        every { userService.updatePassword("1", request) } just Runs

        mockMvc.post("/users/change-password") {
            content = objectMapper.writeValueAsString(request)
            contentType = MediaType.APPLICATION_JSON
        }
            .andExpect {
                status { isOk() }
            }
    }

    @Test
    fun `updateRolesForUser when user not admin return 403`() {
        val request = RoleChangeRequest(roles = listOf(Role.USER, Role.ADMIN))
        every { securityService.isAdmin() } returns false

        mockMvc.post("/users/1/roles") {
            content = objectMapper.writeValueAsString(request)
            contentType = MediaType.APPLICATION_JSON
        }
            .andExpect {
                status { isForbidden() }
            }
    }

    @Test
    fun `updateRolesForUser when given userId and roles and user is admin returns 200`() {
        val request = RoleChangeRequest(roles = listOf(Role.USER, Role.ADMIN))
        every { securityService.isAdmin() } returns true
        every { userService.updateRoleForUser("1", request.roles) } just Runs

        mockMvc.post("/users/1/roles") {
            content = objectMapper.writeValueAsString(request)
            contentType = MediaType.APPLICATION_JSON
        }
            .andExpect {
                status { isOk() }
            }
    }
}