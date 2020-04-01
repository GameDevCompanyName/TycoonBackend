package ru.gdcn.tycoon.storage.repository

import ru.gdcn.tycoon.storage.entity.Road
import ru.gdcn.tycoon.storage.repository.base.BaseDataRepository
import ru.gdcn.tycoon.storage.repository.base.IRoadRepository

class RoadRepositoryImpl : BaseDataRepository<Road>("Road"), IRoadRepository{
    override fun findByCityId(cityId: Long): List<Road> {
        val resultByFrom = findByColumnName("from_city_id", cityId.toString())
        val resultByTo = findByColumnName("to_city_id", cityId.toString())
        resultByFrom.toMutableList().addAll(resultByTo)
        return resultByFrom
    }
}