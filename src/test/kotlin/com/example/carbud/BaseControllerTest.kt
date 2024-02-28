package com.example.carbud

import com.example.carbud.config.SecurityTestConfig
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import

@Import(SecurityTestConfig::class)
abstract class BaseControllerTest : BaseUnitTest() {

    @Autowired
    protected lateinit var objectMapper: ObjectMapper
}
