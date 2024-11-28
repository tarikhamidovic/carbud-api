package com.example.carbud.auth

import com.example.carbud.BaseControllerTest
import com.example.carbud.utils.ObjectMother
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@WebMvcTest(AuthController::class)
class AuthControllerTest : BaseControllerTest() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var authService: AuthService

    private val loginRequest = ObjectMother.loginRequest()
    private val registrationRequest = ObjectMother.registrationRequest()

    @Test
    fun `login when given request body calls login`() {
        every { authService.login(loginRequest) } returns "token"

        mockMvc.post("/login") {
            content = objectMapper.writeValueAsString(loginRequest)
            contentType = MediaType.APPLICATION_JSON
        }
            .andExpect {
                status { isOk() }
                content { string("token") }
            }

        verify { authService.login(loginRequest) }
    }

    @Test
    fun `register when given request body call register`() {
        every { authService.register(registrationRequest) } returns "token"

        mockMvc.post("/register") {
            content = objectMapper.writeValueAsString(registrationRequest)
            contentType = MediaType.APPLICATION_JSON
        }
            .andExpect {
                status { isOk() }
                content { string("token") }
            }

        verify { authService.register(registrationRequest) }
    }
}