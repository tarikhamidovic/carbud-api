package com.example.carbud.manufacturer

import com.example.carbud.auth.SecurityService
import com.example.carbud.auth.exceptions.ActionNotAllowedException
import com.example.carbud.manufacturer.dto.ManufacturerRequest
import com.example.carbud.manufacturer.dto.ManufacturerResponse
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/manufacturers")
class ManufacturerController(
    private val manufacturerService: ManufacturerService,
    private val securityService: SecurityService
) {

    @GetMapping
    fun getAllManufacturers(): List<ManufacturerResponse> {
        return manufacturerService.getAllManufacturers().map { it.toResponse() }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun creteManufacturer(@RequestBody manufacturer: ManufacturerRequest) {
        if (!securityService.isAdmin()) { throw ActionNotAllowedException("User is not admin") }
        manufacturerService.createManufacturer(manufacturer.name, manufacturer.models)
    }

    @PutMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updateManufacturer(@RequestBody manufacturerRequest: ManufacturerRequest) {
        if (!securityService.isAdmin()) { throw ActionNotAllowedException("User is not admin") }
        manufacturerService.updateManufacturer(manufacturerRequest)
    }

    @DeleteMapping("/{manufacturerName}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteManufacturer(@PathVariable manufacturerName: String) {
        if (!securityService.isAdmin()) { throw ActionNotAllowedException("User is not admin") }
        manufacturerService.deleteManufacturer(manufacturerName)
    }
}
