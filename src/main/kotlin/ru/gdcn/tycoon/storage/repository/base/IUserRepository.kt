package ru.gdcn.tycoon.storage.repository.base

import org.hibernate.Session
import ru.gdcn.tycoon.storage.entity.User
import java.util.*

interface IUserRepository {
    fun save(session: Session, user: User): Long?
    fun delete(session: Session, user: User)
    fun findByName(session: Session, name: String): User?
}