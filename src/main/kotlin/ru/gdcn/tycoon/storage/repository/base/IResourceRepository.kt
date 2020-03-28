package ru.gdcn.tycoon.storage.repository.base

import ru.gdcn.tycoon.storage.entity.Resource
import java.util.*

interface IResourceRepository {
    fun findById(id: Long): Optional<Resource>
}