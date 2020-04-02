package ru.gdcn.tycoon.storage.repository

import org.hibernate.Session
import ru.gdcn.tycoon.storage.entity.City
import ru.gdcn.tycoon.storage.repository.base.BaseDataRepository
import ru.gdcn.tycoon.storage.repository.base.ICityRepository

import java.util.*

class CityRepositoryImpl : BaseDataRepository<City>("City"), ICityRepository {

    override fun save(session: Session, city: City): Long? = saveEntity(session, city) as Long?

    override fun findById(session: Session, id: Long): City? = findByPrimaryKey(session, City::class.java, id)

    override fun findAll(session: Session): List<City>? = findAllEntity(session)
}
