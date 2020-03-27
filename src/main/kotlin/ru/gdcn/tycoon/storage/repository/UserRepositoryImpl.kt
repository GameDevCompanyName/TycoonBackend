package ru.gdcn.tycoon.storage.repository

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ru.gdcn.tycoon.storage.StorageHelper
import ru.gdcn.tycoon.storage.entity.User

import java.util.*

class UserRepositoryImpl : BaseDataRepository<User>("User"), UserRepository {

    val logger: Logger by lazy { LoggerFactory.getLogger(UserRepositoryImpl::class.java) }

    override fun save(user: User): Boolean {
        StorageHelper.sessionFactory.openSession().use {
            try {
                it.beginTransaction()
                it.save(user)
                it.transaction.commit()
            } catch (e: Exception) {
                e.printStackTrace()
                return false
            }
        }
        return true
    }

    override fun findByName(name: String): Optional<User> {
        val selectResult = findByColumnName("username", name)
        return if (selectResult.isEmpty()) {
            Optional.empty()
        } else {
            if (selectResult.size > 1) {
                logger.error("Пользователей с именем $name больше одного!")
                Optional.empty()
            } else {
                Optional.of(selectResult.first())
            }
        }
    }
}