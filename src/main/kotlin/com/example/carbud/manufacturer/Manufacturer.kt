package com.example.carbud.manufacturer

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class Manufacturer(
    @Id
    val name: String,
    val models: MutableSet<String>
)
