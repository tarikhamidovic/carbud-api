package com.example.carbud.seller

import com.example.carbud.BaseControllerTest
import com.example.carbud.auth.SecurityService
import com.example.carbud.seller.exceptions.SellerNotFoundException
import com.example.carbud.utils.ObjectMother
import com.example.carbud.vehicle.VehicleService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.*

@WebMvcTest(SellerController::class)
class SellerControllerTest : BaseControllerTest() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var sellerService: SellerService

    @MockkBean
    private lateinit var vehicleService: VehicleService

    @MockkBean
    private lateinit var securityService: SecurityService

    @Test
    fun `getSellerBydId when given sellerId returns 200 and json`() {
        every { sellerService.getSellerById("1") } returns ObjectMother.seller()
        val expected = objectMapper.writeValueAsString(ObjectMother.sellerResponse())

        mockMvc.get("/sellers/1")
            .andExpect {
                status { isOk() }
                content { json(expected) }
            }
    }

    @Test
    fun `getSellerBydId when given non-existing sellerId returns 404`() {
        every { sellerService.getSellerById("0") } throws SellerNotFoundException("")

        mockMvc.get("/sellers/0")
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `getCurrentSeller when called returns 200 and json`() {
        every { securityService.sellerId } returns "1"
        every { sellerService.getSellerById("1") } returns ObjectMother.seller()
        val expected = objectMapper.writeValueAsString(ObjectMother.sellerResponse())

        mockMvc.get("/sellers/current")
            .andExpect {
                status { isOk() }
                content { json(expected) }
            }
    }

    @Test
    fun `getCurrentSeller when no sellerId in security context returns 403`() {
        every { securityService.sellerId } returns null

        mockMvc.get("/sellers/current")
            .andExpect {
                status { isForbidden() }
            }
    }

    @Test
    fun `updateSellerById when given sellerId and sellerRequest and user is not admin returns 403`() {
        every { securityService.isAdmin() } returns false

        mockMvc.put("/sellers/1") {
            content = objectMapper.writeValueAsString(ObjectMother.sellerRequest())
            contentType = MediaType.APPLICATION_JSON
        }
            .andExpect {
                status { isForbidden() }
            }
    }

    @Test
    fun `updateSellerById when given sellerId and sellerRequest and user is admin returns 204`() {
        every { securityService.isAdmin() } returns true
        every { sellerService.updateSeller("1", ObjectMother.sellerRequest()) } returns ObjectMother.seller()

        mockMvc.put("/sellers/1") {
            content = objectMapper.writeValueAsString(ObjectMother.sellerRequest())
            contentType = MediaType.APPLICATION_JSON
        }
            .andExpect {
                status { isNoContent() }
                content { string("") }
            }
    }

    @Test
    fun `updateSeller when given sellerRequest and no sellerId claim in security context returns 403`() {
        every { securityService.sellerId } returns null

        mockMvc.put("/sellers") {
            content = objectMapper.writeValueAsString(ObjectMother.sellerRequest())
            contentType = MediaType.APPLICATION_JSON
        }
            .andExpect {
                status { isForbidden() }
            }
    }

    @Test
    fun `updateSeller when given sellerRequest returns 204`() {
        every { securityService.sellerId } returns "1"
        every { sellerService.updateSeller("1", ObjectMother.sellerRequest()) } returns ObjectMother.seller()

        mockMvc.put("/sellers") {
            content = objectMapper.writeValueAsString(ObjectMother.sellerRequest())
            contentType = MediaType.APPLICATION_JSON
        }
            .andExpect {
                status { isNoContent() }
                content { string("") }
            }
    }

    @Test
    fun `createSeller when given sellerRequest and no userId claim in security context returns 403`() {
        every { securityService.userId } returns null

        mockMvc.post("/sellers") {
            content = objectMapper.writeValueAsString(ObjectMother.sellerRequest())
            contentType = MediaType.APPLICATION_JSON
        }
            .andExpect {
                status { isForbidden() }
            }
    }

    @Test
    fun `createSeller when given sellerId and sellerRequest returns 204`() {
        every { sellerService.createSeller("1", ObjectMother.sellerRequest()) } returns ObjectMother.seller()
        every { securityService.userId } returns "1"

        mockMvc.post("/sellers") {
            content = objectMapper.writeValueAsString(ObjectMother.sellerRequest())
            contentType = MediaType.APPLICATION_JSON
        }
            .andExpect {
                status { isCreated() }
                content { string("") }
            }
    }

    @Test
    fun `deleteSellerBydId when given sellerId and user is not admin returns 403`() {
        every { securityService.isAdmin() } returns false

        mockMvc.delete("/sellers/1")
            .andExpect {
                status { isForbidden() }
            }
    }

    @Test
    fun `deleteSellerBydId when given sellerId and user is admin returns 204`() {
        every { securityService.isAdmin() } returns true
        every { vehicleService.deleteVehiclesBySellerId("1") } just Runs
        every { sellerService.deleteSellerById("1") } just Runs

        mockMvc.delete("/sellers/1")
            .andExpect {
                status { isNoContent() }
            }
    }

    @Test
    fun `deleteSeller when no sellerId claim in security context returns 403`() {
        every { securityService.sellerId } returns null

        mockMvc.delete("/sellers")
            .andExpect {
                status { isForbidden() }
            }
    }

    @Test
    fun `deleteSeller when valid security context returns 204`() {
        every { securityService.sellerId } returns "1"
        every { vehicleService.deleteVehiclesBySellerId("1") } just Runs
        every { sellerService.deleteSellerById("1") } just Runs

        mockMvc.delete("/sellers")
            .andExpect {
                status { isNoContent() }
            }
    }
}