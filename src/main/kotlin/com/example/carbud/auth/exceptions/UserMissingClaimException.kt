package com.example.carbud.auth.exceptions

class UserMissingClaimException(override val message: String?) : RuntimeException(message)
