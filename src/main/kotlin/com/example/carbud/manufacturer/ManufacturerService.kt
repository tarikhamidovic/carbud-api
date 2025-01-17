package com.example.carbud.manufacturer

import com.example.carbud.manufacturer.dto.ManufacturerRequest
import com.example.carbud.manufacturer.exceptions.ManufacturerNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class ManufacturerService(private val manufacturerRepository: ManufacturerRepository) {

    private val logger = LoggerFactory.getLogger(javaClass)

    fun getAllManufacturers(): List<Manufacturer> = manufacturerRepository.findAll()

    fun addModelToManufacturer(manufacturerName: String, model: String) {
        val manufacturer = getManufacturerByName(manufacturerName)

        if (manufacturer.models.contains(model)) {
            logger.info("Model $model already exists for manufacturer $manufacturerName")
            return
        }
        manufacturer.models.add(model)
        manufacturerRepository.save(manufacturer)
    }

    fun getManufacturerByName(name: String): Manufacturer {
        return manufacturerRepository.findManufacturerByName(name)
            ?: throw ManufacturerNotFoundException("Manufacturer with name $name does not exist")
    }

    fun createManufacturer(manufacturerName: String, models: MutableSet<String>) {
        if (manufacturerRepository.findManufacturerByName(manufacturerName) == null) {
            manufacturerRepository.save(
                Manufacturer(manufacturerName, models)
            )
        }
    }

    fun updateManufacturer(manufacturerRequest: ManufacturerRequest): Manufacturer {
        val existingManufacturer = getManufacturerByName(manufacturerRequest.name)

        val updatedManufacturer = existingManufacturer.copy(
            name = manufacturerRequest.name,
            models = manufacturerRequest.models
        )

        return manufacturerRepository.save(updatedManufacturer)
    }

    fun deleteManufacturer(manufacturerName: String) {
        manufacturerRepository.deleteManufacturerByName(manufacturerName)
    }
}
