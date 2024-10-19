package com.example.carbud.vehicle

import com.example.carbud.BaseControllerTest
import com.example.carbud.utils.ObjectMother
import com.example.carbud.vehicle.exceptions.VehicleNotFoundException
import com.ninjasquad.springmockk.MockkBean
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.*

@WebMvcTest(VehicleController::class)
class VehicleControllerTest : BaseControllerTest() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var vehicleService: VehicleService

    @Test
    fun `getFilteredVehicle when given filters returns 200 and json`() {
        val page = PageImpl(
            listOf(ObjectMother.vehicleResponse),
            PageRequest.of(0, 10),
            1
        )
        every { vehicleService.getFilteredVehicles(any()) } returns page

        mockMvc.get("/vehicles")
            .andExpect {
                status { isOk() }
            }
    }

    @Test
    fun `getVehicleById when given id of existing vehicle should return 200 and json`() {
        every { vehicleService.getVehicleById("abc123") } returns ObjectMother.vehicle
        val expected = objectMapper.writeValueAsString(ObjectMother.vehicle)

        mockMvc.get("/vehicles/abc123")
            .andExpect {
                status { isOk() }
                content { json(expected) }
            }
    }

    @Test
    fun `getVehicleById when given id of non-existing vehicle should return 404`() {
        every { vehicleService.getVehicleById("no123") } throws VehicleNotFoundException("")

        mockMvc.get("/vehicles/no123")
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `postVehicle when given VehicleRequest should return 201`() {
        val request = ObjectMother.vehicleRequest
        every { vehicleService.createVehicle(request) } returns request.toEntity()

        mockMvc
            .post("/vehicles") {
                content = objectMapper.writeValueAsString(request)
                contentType = MediaType.APPLICATION_JSON
            }
            .andExpect {
                status { isCreated() }
                content { string("") }
            }
    }

    @Test
    fun `updateVehicle when given VehicleRequest and vehicle which exists should return 204`() {
        val request = ObjectMother.vehicleRequest
        every { vehicleService.updateVehicle(any(), request) } returns request.toEntity()

        mockMvc
            .put("/vehicles/abc123") {
                content = objectMapper.writeValueAsString(request)
                contentType = MediaType.APPLICATION_JSON
            }
            .andExpect {
                status { isNoContent() }
                content { string("") }
            }
    }

    @Test
    fun `updateVehicle when given VehicleRequest and vehicle not exists should return 404`() {
        val request = ObjectMother.vehicleRequest
        every { vehicleService.updateVehicle(any(), request) } throws VehicleNotFoundException("")

        mockMvc
            .put("/vehicles/abc123") {
                content = objectMapper.writeValueAsString(request)
                contentType = MediaType.APPLICATION_JSON
            }
            .andExpect { status { isNotFound() } }
    }

    @Test
    fun `deleteVehicleById when given vehicleId should return 204`() {
        every { vehicleService.deleteVehicleById("abc123") } just Runs

        mockMvc.delete("/vehicles/abc123")
            .andExpect { status { isNoContent() } }
    }
}