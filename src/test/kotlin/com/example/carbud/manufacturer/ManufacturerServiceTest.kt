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
}