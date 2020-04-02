package ru.gdcn.tycoon.storage.repository.base

import org.hibernate.Session
import ru.gdcn.tycoon.storage.entity.CompositeKeyRoad
import ru.gdcn.tycoon.storage.entity.Road

interface IRoadRepository {
    fun findByCityId(session: Session, cityId: Long): List<Road>?
    fun save(session: Session, road: Road): CompositeKeyRoad?
}