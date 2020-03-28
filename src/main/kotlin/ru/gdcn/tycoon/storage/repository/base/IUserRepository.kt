package ru.gdcn.tycoon.storage.repository.base

import ru.gdcn.tycoon.storage.entity.User
import java.util.*

interface IUserRepository {
    fun save(user: User): Long
    fun delete(user: User)
    fun findByName(name: String): Optional<User>
}