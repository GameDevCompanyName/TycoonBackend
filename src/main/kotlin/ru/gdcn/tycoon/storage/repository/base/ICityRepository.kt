package ru.gdcn.tycoon.storage.repository.base

import ru.gdcn.tycoon.storage.entity.City
import java.util.*

interface ICityRepository {
    fun save(city: City): Long
    fun findById(id: Long): Optional<City>
    fun findAll(): List<City>
}