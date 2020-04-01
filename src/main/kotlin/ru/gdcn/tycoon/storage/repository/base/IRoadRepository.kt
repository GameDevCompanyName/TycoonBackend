package ru.gdcn.tycoon.storage.repository.base

import ru.gdcn.tycoon.storage.entity.Road

interface IRoadRepository {
    fun findByCityId(cityId: Long): List<Road>
}