package ru.gdcn.tycoon.storage.repository.base

import org.hibernate.Session
import ru.gdcn.tycoon.storage.entity.CityResource
import ru.gdcn.tycoon.storage.entity.CompositeKeyCityResource
import java.util.*

interface ICityResourceRepository {
    fun findByCompositeId(session: Session, id: CompositeKeyCityResource): CityResource?
    fun findByCityId(session: Session, cityId: Long): List<CityResource>?
    fun save(session: Session, cityResource: CityResource): CompositeKeyCityResource?
    fun update(session: Session, cityResource: CityResource)
}