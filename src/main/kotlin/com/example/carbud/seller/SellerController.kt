package com.example.carbud.seller

import com.example.carbud.auth.SecurityService
import com.example.carbud.auth.exceptions.ActionNotAllowedException
import com.example.carbud.auth.exceptions.UserMissingClaimException
import com.example.carbud.seller.dto.SellerRequest
import com.example.carbud.seller.dto.SellerResponse
import com.example.carbud.vehicle.VehicleService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/sellers")
class SellerController(
    private val sellerService: SellerService,
    private val vehicleService: VehicleService,
    private val securityService: SecurityService
) {

    @GetMapping("/{sellerId}")
    fun getSellerById(@PathVariable sellerId: String) = sellerService.getSellerById(sellerId).toResponse()

    @GetMapping("/current")
    fun getCurrentSeller(): SellerResponse {
        val sellerId = securityService.sellerId ?:
            throw UserMissingClaimException("Security context missing claim sellerId")

        return sellerService.getSellerById(sellerId).toResponse()
    }

    @PutMapping("/{sellerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updateSellerById(@PathVariable sellerId: String, @RequestBody sellerRequest: SellerRequest) {
        if (!securityService.isAdmin()) { throw ActionNotAllowedException("User is not admin") }
        sellerService.updateSeller(sellerId, sellerRequest)
    }

    @PutMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updateSeller(@RequestBody sellerRequest: SellerRequest) {
        val sellerId = securityService.sellerId ?:
            throw UserMissingClaimException("Security context missing claim sellerId")

        sellerService.updateSeller(sellerId, sellerRequest)
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createSeller(@RequestBody sellerRequest: SellerRequest) {
        val userId = securityService.userId ?: throw UserMissingClaimException("Security context missing claim userId")
        sellerService.createSeller(userId, sellerRequest)
    }

    @DeleteMapping("/{sellerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteSellerById(@PathVariable sellerId: String) {
        if (!securityService.isAdmin()) { throw ActionNotAllowedException("User is not admin") }
        vehicleService.deleteVehiclesBySellerId(sellerId)
        sellerService.deleteSellerById(sellerId)
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteSeller() {
        val sellerId = securityService.sellerId
            ?: throw UserMissingClaimException("Security context missing claim sellerId")

        vehicleService.deleteVehiclesBySellerId(sellerId)
        sellerService.deleteSellerById(sellerId)
    }
}
