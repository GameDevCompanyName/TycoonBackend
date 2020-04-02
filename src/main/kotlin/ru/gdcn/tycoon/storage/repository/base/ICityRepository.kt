package ru.gdcn.tycoon.storage.repository.base

import org.hibernate.Session
import ru.gdcn.tycoon.storage.entity.City
import java.util.*

interface ICityRepository {
    fun save(session: Session, city: City): Long?
    fun findById(session: Session, id: Long): City?
    fun findAll(session: Session): List<City>?
}