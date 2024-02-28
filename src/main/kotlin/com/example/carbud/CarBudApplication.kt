package com.example.carbud

import com.example.carbud.config.RsaKeyProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(RsaKeyProperties::class)
class CarBudApplication

fun main(args: Array<String>) {
	runApplication<CarBudApplication>(*args)
}
