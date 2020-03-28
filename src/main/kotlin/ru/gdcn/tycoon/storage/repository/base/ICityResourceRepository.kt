package ru.gdcn.tycoon.storage.repository.base

import ru.gdcn.tycoon.storage.entity.CityResource
import ru.gdcn.tycoon.storage.entity.CompositeKeyCityResource
import java.util.*

interface ICityResourceRepository {
    fun findByCompositeId(id: CompositeKeyCityResource): Optional<CityResource>
    fun findByCityId(cityId: Long): List<CityResource>
}