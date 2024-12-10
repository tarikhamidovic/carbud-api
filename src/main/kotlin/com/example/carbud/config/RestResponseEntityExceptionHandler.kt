package com.example.carbud.config

import com.example.carbud.auth.exceptions.ActionNotAllowedException
import com.example.carbud.auth.exceptions.UserAlreadyExistsException
import com.example.carbud.auth.exceptions.UserMissingClaimException
import com.example.carbud.auth.exceptions.UserNotFoundException
import com.example.carbud.manufacturer.exceptions.ManufacturerNotFoundException
import com.example.carbud.seller.exceptions.SellerAssignedToUserException
import com.example.carbud.seller.exceptions.SellerNotFoundException
import com.example.carbud.vehicle.exceptions.VehicleNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class RestResponseEntityExceptionHandler : ResponseEntityExceptionHandler() {

    companion object {
        private const val AUTH_ERROR_MESSAGE = "Provided username or password is invalid"
    }

    @ExceptionHandler(SellerNotFoundException::class)
    fun handleSellerNotFoundException(ex: SellerNotFoundException) =
        ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ex.message?.let { ApiErrorResponse(it) })

    @ExceptionHandler(VehicleNotFoundException::class)
    fun handleVehicleNotFoundException(ex: VehicleNotFoundException) =
        ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ex.message?.let { ApiErrorResponse(it) })

    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFoundException(ex: UserNotFoundException): ResponseEntity<ApiErrorResponse> {
        logger.error("UserNotFoundException occurred", ex)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiErrorResponse(ex.message))
    }

    @ExceptionHandler(UserAlreadyExistsException::class)
    fun handleUserAlreadyExistsException(ex: UserAlreadyExistsException): ResponseEntity<ApiErrorResponse> {
        logger.error("UserAlreadyExistsException occurred", ex)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiErrorResponse(ex.message))
    }

    @ExceptionHandler(ManufacturerNotFoundException::class)
    fun handleManufacturerNotFoundException(ex: ManufacturerNotFoundException) =
        ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ex.message?.let { ApiErrorResponse(it) })

    @ExceptionHandler(UserMissingClaimException::class)
    fun handleUserMissingClaimException(ex: UserMissingClaimException): ResponseEntity<Unit> {
        logger.error("UserMissingClaimException occurred", ex)
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
    }

    @ExceptionHandler(ActionNotAllowedException::class)
    fun handleActionNotAllowedException(ex: ActionNotAllowedException): ResponseEntity<Unit> {
        logger.error("ActionNotAllowedException occurred", ex)
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
    }

    @ExceptionHandler(SellerAssignedToUserException::class)
    fun handleSellerAssignedToUserException(ex: SellerAssignedToUserException) =
        ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ex.message?.let { ApiErrorResponse(it) })

    data class ApiErrorResponse(val message: String)
}
