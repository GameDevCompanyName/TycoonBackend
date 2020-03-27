package ru.gdcn.tycoon.storage.repository

import ru.gdcn.tycoon.storage.entity.User
import java.util.*

interface UserRepository {
    fun save(user: User): Boolean
    fun findByName(name: String): Optional<User>
}