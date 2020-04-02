package ru.gdcn.tycoon.storage.repository

import org.hibernate.Session
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ru.gdcn.tycoon.storage.StorageHelper
import ru.gdcn.tycoon.storage.entity.User
import ru.gdcn.tycoon.storage.repository.base.BaseDataRepository
import ru.gdcn.tycoon.storage.repository.base.IUserRepository

import java.util.*

class UserRepositoryImpl : BaseDataRepository<User>("User"), IUserRepository {

    private val logger: Logger by lazy { LoggerFactory.getLogger(UserRepositoryImpl::class.java) }

    override fun save(session: Session, user: User): Long? = saveEntity(session, user) as Long?

    override fun delete(session: Session, user: User) = deleteEntity(session, user)

    override fun findByName(session: Session, name: String): User? {
        val selectResult = findByColumnName(session, "username", name)
        return if (selectResult == null || selectResult.isEmpty()) {
            null
        } else {
            if (selectResult.size > 1) {
                logger.error("Пользователей с именем $name больше одного!")
                null
            } else {
                selectResult.first()
            }
        }
    }
}