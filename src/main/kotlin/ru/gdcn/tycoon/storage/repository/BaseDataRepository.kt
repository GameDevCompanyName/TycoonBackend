package ru.gdcn.tycoon.storage.repository

import ru.gdcn.tycoon.storage.StorageHelper
import java.lang.Exception

open class BaseDataRepository<T>(private val entityName: String) {
    protected fun findByColumnName(columnName: String, whereParam: String): List<T> {
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
}