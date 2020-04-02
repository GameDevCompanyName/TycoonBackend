package ru.gdcn.tycoon.storage.repository

import org.hibernate.Session
import ru.gdcn.tycoon.storage.entity.Resource
import ru.gdcn.tycoon.storage.repository.base.BaseDataRepository
import ru.gdcn.tycoon.storage.repository.base.IResourceRepository
import java.util.*

class ResourceRepositoryImpl : BaseDataRepository<Resource>("Resource"), IResourceRepository {
    override fun findById(session: Session, id: Long): Resource? = findByPrimaryKey(session, Resource::class.java, id)
    override fun save(session: Session, resource: Resource): Long? = saveEntity(session, resource) as Long?
}
