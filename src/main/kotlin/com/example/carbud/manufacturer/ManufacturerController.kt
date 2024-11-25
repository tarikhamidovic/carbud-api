package com.example.carbud.manufacturer

import com.example.carbud.manufacturer.dto.ManufacturerResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/manufacturers")
class ManufacturerController(
    private val manufacturerService: ManufacturerService
) {
    @GetMapping
    fun getAllManufacturers(): List<ManufacturerResponse> {
        return manufacturerService.getAllManufacturers().map { it.toResponse() }
    }

    // TODO: Test endpoint
    @PostMapping
    fun creteManufacturer(@RequestBody manufacturer: ManufacturerResponse) {
        manufacturerService.createManufacturer(manufacturer.name, manufacturer.models)
    }
}
