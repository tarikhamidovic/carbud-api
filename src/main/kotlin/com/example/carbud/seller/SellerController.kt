package com.example.carbud.seller

import com.example.carbud.seller.dto.SellerRequest
import com.example.carbud.vehicle.dto.VehicleRequest
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/sellers")
class SellerController(private val sellerService: SellerService) {

    @GetMapping("/{sellerId}")
    fun getSellerById(@PathVariable sellerId: String) = sellerService.getSellerById(sellerId).toResponse()

    @PutMapping("/{sellerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updateSeller(@PathVariable sellerId: String, @RequestBody sellerRequest: SellerRequest) {
        sellerService.updateSeller(sellerId, sellerRequest)
    }

    @PostMapping("/{sellerId}/vehicles")
    @ResponseStatus(HttpStatus.CREATED)
    fun createVehicleForSeller(@PathVariable sellerId: String, @RequestBody vehicleRequest: VehicleRequest) {
        sellerService.createVehicleForSeller(sellerId, vehicleRequest)
    }

    @DeleteMapping("/{sellerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteSellerById(@PathVariable sellerId: String) = sellerService.deleteSellerById(sellerId)
}
