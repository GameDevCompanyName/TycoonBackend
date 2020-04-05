package ru.gdcn.tycoon.storage.repository

import org.hibernate.Session
import ru.gdcn.tycoon.storage.entity.CityResource
import ru.gdcn.tycoon.storage.entity.CompositeKeyCityResource
import ru.gdcn.tycoon.storage.repository.base.BaseDataRepository
import ru.gdcn.tycoon.storage.repository.base.ICityResourceRepository
import java.util.*

class CityResourceRepositoryImpl : BaseDataRepository<CityResource>("CityResource"), ICityResourceRepository {
    override fun findByCompositeId(session: Session, id: CompositeKeyCityResource): CityResource?
            = findByPrimaryKey(session, CityResource::class.java, id)

    override fun findByCityId(session: Session, cityId: Long): List<CityResource>?
            = findByColumnName(session,"city_id", cityId.toString())

    override fun save(session: Session, cityResource: CityResource): CompositeKeyCityResource?
            = saveEntity(session, cityResource) as CompositeKeyCityResource?

    override fun update(session: Session, cityResource: CityResource) = updateEntity(session, cityResource)
}