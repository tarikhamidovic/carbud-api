package com.example.carbud.config

import com.example.carbud.seller.exceptions.SellerNotFoundException
import com.example.carbud.vehicle.exceptions.VehicleNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class RestResponseEntityExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(SellerNotFoundException::class)
    fun handleSellerNotFoundException(ex: SellerNotFoundException) =
        ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.message)

    @ExceptionHandler(VehicleNotFoundException::class)
    fun handleVehicleNotFoundException(ex: VehicleNotFoundException, request: WebRequest) =
        ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.message)
}
