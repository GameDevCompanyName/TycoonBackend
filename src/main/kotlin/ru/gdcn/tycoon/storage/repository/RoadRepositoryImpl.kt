package ru.gdcn.tycoon.storage.repository

import org.hibernate.Session
import ru.gdcn.tycoon.storage.entity.CompositeKeyRoad
import ru.gdcn.tycoon.storage.entity.Road
import ru.gdcn.tycoon.storage.repository.base.BaseDataRepository
import ru.gdcn.tycoon.storage.repository.base.IRoadRepository

class RoadRepositoryImpl : BaseDataRepository<Road>("Road"), IRoadRepository {
    override fun findByCityId(session: Session, cityId: Long): List<Road>? {
        val resultByFrom = findByColumnName(session, "from_city_id", cityId.toString())
        val resultByTo = findByColumnName(session, "to_city_id", cityId.toString())
        return when {
            resultByFrom != null -> {
                val result = resultByFrom.toMutableList()
                if (resultByTo != null) {
                    result.addAll(resultByTo)
                }
                result
            }
            resultByTo != null -> resultByTo
            else -> null
        }
    }

    override fun save(session: Session, road: Road): CompositeKeyRoad? = saveEntity(session, road) as CompositeKeyRoad?
}
