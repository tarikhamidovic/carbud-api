package com.example.carbud.seller

import com.example.carbud.BaseControllerTest
import com.example.carbud.utils.ObjectMother
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

    @Test
    fun `getSellerBydId when given sellerId returns 200 and json`() {
        every { sellerService.getSellerById("1") } returns ObjectMother.sellerResponse
        val expected = objectMapper.writeValueAsString(ObjectMother.sellerResponse)

        mockMvc.get("/sellers/1")
            .andExpect {
                status { isOk() }
                content { json(expected) }
            }
    }

    @Test
    fun `updateSeller when given sellerId and sellerRequest returns 204`() {
        every { sellerService.updateSeller("1", ObjectMother.sellerRequest) } returns ObjectMother.sellerResponse

        mockMvc.put("/sellers/1") {
            content = objectMapper.writeValueAsString(ObjectMother.sellerRequest)
            contentType = MediaType.APPLICATION_JSON
        }
            .andExpect {
                status { isNoContent() }
                content { string("") }
            }
    }

    @Test
    fun `deleteSellerBydId when given sellerId returns 204`() {
        every { sellerService.deleteSellerById("1") } just Runs

        mockMvc.delete("/sellers/1")
            .andExpect {
                status { isNoContent() }
            }
    }
}