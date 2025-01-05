package com.example.carbud.auth.exceptions

class UserAlreadyExistsException(override val message: String) : RuntimeException(message)
