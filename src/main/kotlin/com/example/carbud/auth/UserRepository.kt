package com.example.carbud.auth

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : MongoRepository<User, String> {
    fun findUserByUserName(username: String): User?
    fun findUserById(userId: String): User?
}
