package com.example.carbud.config

import com.example.carbud.seller.exceptions.SellerNotFoundException
import com.example.carbud.vehicle.exceptions.VehicleNotFoundException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class RestResponseEntityExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(SellerNotFoundException::class)
    fun handleSellerNotFoundException(ex: SellerNotFoundException) =
        ResponseEntity.notFound().build<String>()

    @ExceptionHandler(VehicleNotFoundException::class)
    fun handleVehicleNotFoundException(ex: RuntimeException, request: WebRequest) =
        ResponseEntity.notFound().build<String>()
}
