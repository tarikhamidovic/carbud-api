package com.example.carbud.seller

import com.example.carbud.seller.dto.SellerRequest
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/sellers")
class SellerController(private val sellerService: SellerService) {

    @GetMapping("/{sellerId}")
    fun getSellerById(@PathVariable sellerId: String) = sellerService.getSellerById(sellerId)

    @PutMapping("/{sellerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updateSeller(@PathVariable sellerId: String, @RequestBody sellerRequest: SellerRequest) {
        sellerService.updateSeller(sellerId, sellerRequest)
    }

    @DeleteMapping("/{sellerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteSellerById(@PathVariable sellerId: String) = sellerService.deleteSellerById(sellerId)
}