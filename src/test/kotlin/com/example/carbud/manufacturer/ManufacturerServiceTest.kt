package com.example.carbud.manufacturer

import com.example.carbud.BaseUnitTest
import com.example.carbud.manufacturer.exceptions.ManufacturerNotFoundException
import com.example.carbud.utils.ObjectMother
import io.mockk.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ManufacturerServiceTest : BaseUnitTest() {

    private val manufacturer = ObjectMother.manufacturer()

    private val manufacturerRepository = mockk<ManufacturerRepository> {
        every { findAll() } returns listOf(manufacturer)
        every { findManufacturerByName("Audi") } returns manufacturer
        every { findManufacturerByName("Nokia") } returns null
    }

    private val manufacturerService = ManufacturerService(manufacturerRepository)

    @Test
    fun `getAllManufacturers when called returns list of manufacturers`() {
        val expected = listOf(manufacturer)
        val result = manufacturerService.getAllManufacturers()
        assertEquals(expected, result)
    }

    @Test
    fun `addModelToManufacturer when given valid manufacturer and model appends model to manufacturer models list`() {
        val manufacturerSlot = slot<Manufacturer>()
        every { manufacturerRepository.save(capture(manufacturerSlot)) } returns mockk()

        manufacturerService.addModelToManufacturer("Audi", "S3")

        verify { manufacturerRepository.save(any()) }
        assert(manufacturerSlot.captured.models.contains("S3"))
    }

    @Test
    fun `addModelToManufacturer when given valid manufacturer and model already assigned to manufacturer does not persist`() {
        val manufacturer = ObjectMother.manufacturer()
        every { manufacturerRepository.findManufacturerByName("Audi") } returns manufacturer

        manufacturerService.addModelToManufacturer("Audi", "A3")

        verify(exactly = 0) { manufacturerRepository.save(any()) }
    }

    @Test
    fun `getManufacturerByName when given valid manufacturer name returns manufacturer`() {
        val result = manufacturerService.getManufacturerByName("Audi")
        assertEquals(manufacturer, result)
    }

    @Test
    fun `getManufacturerByName when given non existing manufacturer throws ManufacturerNotFoundException`() {
        assertThrows<ManufacturerNotFoundException> {
            manufacturerService.getManufacturerByName("Nokia")
        }
    }

    @Test
    fun `createManufacturer when given manufacturer name and models persists new manufacturer`() {
        val name = "Audi"
        val models = mutableSetOf("S3", "S4")
        every { manufacturerRepository.findManufacturerByName(name) } returns null
        every { manufacturerRepository.save(any()) } returns mockk()

        val expected = Manufacturer(name, models)
        manufacturerService.createManufacturer(name, models)

        verify { manufacturerRepository.save(expected) }
    }

    @Test
    fun `createManufacturer when given manufacturer name and models but manufacturer exists does not persists new manufacturer`() {
        val manufacturer = ObjectMother.manufacturer()
        every { manufacturerRepository.findManufacturerByName(manufacturer.name) } returns manufacturer
        every { manufacturerRepository.save(any()) } returns mockk()

        manufacturerService.createManufacturer(manufacturer.name, manufacturer.models)

        verify(exactly = 0) { manufacturerRepository.save(any()) }
    }

    @Test
    fun `updateManufacturer when given manufacturerRequests updates existing manufacturer`() {
        val manufacturer = ObjectMother.manufacturer()
        val models = mutableSetOf("S3", "S4")
        val request = ObjectMother.manufacturerRequest().copy(models = models)
        every { manufacturerRepository.findManufacturerByName(manufacturer.name) } returns manufacturer
        every { manufacturerRepository.save(any()) } returns mockk()

        val expected = Manufacturer(request.name, request.models)
        manufacturerService.updateManufacturer(request)

        verify { manufacturerRepository.save(expected) }
    }

    @Test
    fun `deleteManufacturer when given manufacturerId deletes manufacturer`() {
        every { manufacturerRepository.deleteManufacturerByName("Audi") } just Runs
        manufacturerService.deleteManufacturer("Audi")
        verify { manufacturerRepository.deleteManufacturerByName("Audi") }
    }
}