package ru.gdcn.tycoon.storage.repository.base

import ru.gdcn.tycoon.storage.StorageHelper

import java.lang.Exception
import java.util.*

open class BaseDataRepository<T>(private val entityName: String) {

    fun saveEntity(entity: T): Long {
        var id = -1L
        StorageHelper.sessionFactory.openSession().use {
            try {
                it.beginTransaction()
                id = it.save(entity) as Long
                it.transaction.commit()
            } catch (e: Exception) {
                e.printStackTrace()
                return -1L
            }
        }
        return id
    }

    fun deleteEntity(entity: T) {
        StorageHelper.sessionFactory.openSession().use {
            try {
                it.beginTransaction()
                it.delete(entity)
                it.transaction.commit()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun findByPrimaryKey(clazz: Class<T>, primaryKey: Any): Optional<T> {
        var result = Optional.empty<T>()
        StorageHelper.sessionFactory.openSession().use {
            try {
                it.beginTransaction()
                result = Optional.of(it.find(clazz, primaryKey))
                it.transaction.commit()
            } catch (e: Exception) {
                e.printStackTrace()
                return result
            }
        }
        return result
    }

    fun findByColumnName(columnName: String, whereParam: String): List<T> {
        val queryString = "FROM $entityName WHERE $columnName = \'$whereParam\'"
        StorageHelper.sessionFactory.openSession().use {
            return try {
                it.beginTransaction()
                val result = it.createQuery(queryString).list() as List<T>
                it.transaction.commit()
                result
            } catch (e: Exception) {
                e.printStackTrace()
                listOf()
            }
        }
    }

    fun findAllEntity(): List<T> {
        val queryString = "FROM $entityName"
        StorageHelper.sessionFactory.openSession().use {
            return try {
                it.beginTransaction()
                val result = it.createQuery(queryString).list() as List<T>
                it.transaction.commit()
                result
            } catch (e: Exception) {
                e.printStackTrace()
                listOf()
            }
        }
    }
}