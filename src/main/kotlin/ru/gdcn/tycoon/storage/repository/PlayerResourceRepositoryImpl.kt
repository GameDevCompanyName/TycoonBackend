package ru.gdcn.tycoon.storage.repository

import ru.gdcn.tycoon.storage.entity.CompositeKeyPlayerResource
import ru.gdcn.tycoon.storage.entity.PlayerResource
import ru.gdcn.tycoon.storage.repository.base.BaseDataRepository
import ru.gdcn.tycoon.storage.repository.base.IPlayerResourceRepository
import java.util.*

class PlayerResourceRepositoryImpl :
    BaseDataRepository<PlayerResource>("PlayerResource"),
    IPlayerResourceRepository
{
    override fun findByCompositeId(id: CompositeKeyPlayerResource): Optional<PlayerResource>
            = findByPrimaryKey(PlayerResource::class.java, id)

    override fun findByPlayerId(playerId: Long): List<PlayerResource>
            = findByColumnName("player_id", playerId.toString())
}