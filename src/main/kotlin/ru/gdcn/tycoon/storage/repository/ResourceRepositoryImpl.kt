package ru.gdcn.tycoon.storage.repository

import ru.gdcn.tycoon.storage.entity.Resource
import ru.gdcn.tycoon.storage.repository.base.BaseDataRepository
import ru.gdcn.tycoon.storage.repository.base.IResourceRepository
import java.util.*

class ResourceRepositoryImpl : BaseDataRepository<Resource>("Resource"), IResourceRepository {
    override fun findById(id: Long): Optional<Resource> = findByPrimaryKey(Resource::class.java, id)
}