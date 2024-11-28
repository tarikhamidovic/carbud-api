package com.example.carbud.manufacturer

import com.example.carbud.BaseControllerTest
import com.example.carbud.utils.ObjectMother
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@WebMvcTest(ManufacturerController::class)
class ManufacturerControllerTest : BaseControllerTest() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var manufacturerService: ManufacturerService

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
}
