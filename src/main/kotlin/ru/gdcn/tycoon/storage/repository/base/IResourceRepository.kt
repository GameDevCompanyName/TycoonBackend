package ru.gdcn.tycoon.storage.repository.base

import org.hibernate.Session
import ru.gdcn.tycoon.storage.entity.Resource
import java.util.*

interface IResourceRepository {
    fun findById(session: Session, id: Long): Resource?
    fun save(session: Session, resource: Resource): Long?
}
