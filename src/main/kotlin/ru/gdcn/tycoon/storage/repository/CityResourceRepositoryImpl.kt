package ru.gdcn.tycoon.storage.repository

import ru.gdcn.tycoon.storage.entity.CityResource
import ru.gdcn.tycoon.storage.entity.CompositeKeyCityResource
import ru.gdcn.tycoon.storage.repository.base.BaseDataRepository
import ru.gdcn.tycoon.storage.repository.base.ICityResourceRepository
import java.util.*

class CityResourceRepositoryImpl : BaseDataRepository<CityResource>("CityResource"), ICityResourceRepository {
    override fun findByCompositeId(id: CompositeKeyCityResource): Optional<CityResource>
            = findByPrimaryKey(CityResource::class.java, id)

    override fun findByCityId(cityId: Long): List<CityResource> = findByColumnName("city_id", cityId.toString())
}