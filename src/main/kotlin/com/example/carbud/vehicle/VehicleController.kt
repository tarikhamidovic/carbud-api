package com.example.carbud.vehicle

import com.example.carbud.vehicle.dto.VehicleRequest
import com.example.carbud.vehicle.dto.VehicleResponse
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/vehicles")
class VehicleController(private val vehicleService: VehicleService) {

    @GetMapping
    fun getFilteredVehicles(@RequestParam params: Map<String, String>): Page<VehicleResponse> {
        return vehicleService.getFilteredVehicles(params).map { it.toResponse() }
    }

    @GetMapping("/{vehicleId}")
    fun getVehicleById(@PathVariable vehicleId: String) = vehicleService.getVehicleById(vehicleId)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun postVehicle(@RequestBody vehicleRequest: VehicleRequest) {
        vehicleService.createVehicle(vehicleRequest)
    }

    @PutMapping("/{vehicleId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updateVehicle(@PathVariable vehicleId: String, @RequestBody vehicleRequest: VehicleRequest) {
        vehicleService.updateVehicle(vehicleId, vehicleRequest)
    }

    @DeleteMapping("/{vehicleId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteVehicleById(@PathVariable vehicleId: String) = vehicleService.deleteVehicleById(vehicleId)
}
