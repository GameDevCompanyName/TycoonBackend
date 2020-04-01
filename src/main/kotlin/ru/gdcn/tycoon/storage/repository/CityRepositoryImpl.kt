package ru.gdcn.tycoon.storage.repository

import ru.gdcn.tycoon.storage.entity.City
import ru.gdcn.tycoon.storage.repository.base.BaseDataRepository
import ru.gdcn.tycoon.storage.repository.base.ICityRepository

import java.util.*

class CityRepositoryImpl : BaseDataRepository<City>("City"), ICityRepository {

    override fun save(city: City): Long = saveEntity(city)

    override fun findById(id: Long): Optional<City> = findByPrimaryKey(City::class.java, id)

    override fun findAll(): List<City> = findAllEntity()
}