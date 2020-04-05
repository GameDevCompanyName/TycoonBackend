package ru.gdcn.tycoon.storage.repository.base

import org.hibernate.Session

import java.io.Serializable

open class BaseDataRepository<T>(private val entityName: String) {

    fun saveEntity(session: Session, entity: T): Serializable? = session.save(entity)

    fun saveOrUpdateEntity(session: Session, entity: T) = session.saveOrUpdate(entity)

    fun updateEntity(session: Session, entity: T) = session.update(entity)

    fun deleteEntity(session: Session, entity: T) = session.delete(entity)

    fun findByPrimaryKey(session: Session, entityClass: Class<T>, primaryKey: Any): T?
            = session.find(entityClass, primaryKey)

    fun findByColumnName(session: Session, columnName: String, whereParam: String): List<T>? {
        val queryString = "FROM $entityName WHERE $columnName = \'$whereParam\'"
        return session.createQuery(queryString).list() as List<T>?
    }

    fun findAllEntity(session: Session): List<T>? {
        val queryString = "FROM $entityName"
        return session.createQuery(queryString).list() as List<T>?
    }
}