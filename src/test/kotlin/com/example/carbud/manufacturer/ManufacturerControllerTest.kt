package com.example.carbud.manufacturer

import com.example.carbud.BaseControllerTest
import com.example.carbud.auth.SecurityService
import com.example.carbud.utils.ObjectMother
import com.ninjasquad.springmockk.MockkBean
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.*

@WebMvcTest(ManufacturerController::class)
class ManufacturerControllerTest : BaseControllerTest() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var manufacturerService: ManufacturerService

    @MockkBean
    private lateinit var securityService: SecurityService

    @Test
    fun `getAllManufacturers when requested returns 200 and json`() {
        every { manufacturerService.getAllManufacturers() } returns listOf(ObjectMother.manufacturer())
        val expected = objectMapper.writeValueAsString(listOf(ObjectMother.manufacturer()))

        mockMvc.get("/manufacturers")
            .andExpect {
                status { isOk() }
                content { json(expected) }
            }
    }

    @Test
    fun `creteManufacturer when user not admin returns 403`() {
        every { securityService.isAdmin() } returns false

        mockMvc.post("/manufacturers") {
            content = objectMapper.writeValueAsString(ObjectMother.manufacturerRequest())
            contentType = MediaType.APPLICATION_JSON
        }
            .andExpect {
                status { isForbidden() }
            }
    }

    @Test
    fun `creteManufacturer when given manufacturerRequest and user is admin returns 201`() {
        every { securityService.isAdmin() } returns true
        every { manufacturerService.createManufacturer(any(), any()) } just Runs

        mockMvc.post("/manufacturers") {
            content = objectMapper.writeValueAsString(ObjectMother.manufacturerRequest())
            contentType = MediaType.APPLICATION_JSON
        }
            .andExpect {
                status { isCreated() }
            }
    }

    @Test
    fun `updateManufacturer when user not admin returns 403`() {
        every { securityService.isAdmin() } returns false

        mockMvc.put("/manufacturers") {
            content = objectMapper.writeValueAsString(ObjectMother.manufacturerRequest())
            contentType = MediaType.APPLICATION_JSON
        }
            .andExpect {
                status { isForbidden() }
            }
    }

    @Test
    fun `updateManufacturer when given manufacturerRequest and user is admin returns 201`() {
        every { securityService.isAdmin() } returns true
        every { manufacturerService.updateManufacturer(any()) } returns mockk()

        mockMvc.put("/manufacturers") {
            content = objectMapper.writeValueAsString(ObjectMother.manufacturerRequest())
            contentType = MediaType.APPLICATION_JSON
        }
            .andExpect {
                status { isNoContent() }
            }
    }

    @Test
    fun `deleteManufacturer when user not admin returns 403`() {
        every { securityService.isAdmin() } returns false

        mockMvc.delete("/manufacturers/audi")
            .andExpect {
                status { isForbidden() }
            }
    }

    @Test
    fun `deleteManufacturer when given manufacturerName and user is admin returns 201`() {
        every { securityService.isAdmin() } returns true
        every { manufacturerService.deleteManufacturer(any()) } just Runs

        mockMvc.delete("/manufacturers/audi")
            .andExpect {
                status { isNoContent() }
            }
    }
}
