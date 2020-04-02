package ru.gdcn.tycoon.storage.repository

import org.hibernate.Session
import ru.gdcn.tycoon.storage.entity.CompositeKeyPlayerResource
import ru.gdcn.tycoon.storage.entity.PlayerResource
import ru.gdcn.tycoon.storage.repository.base.BaseDataRepository
import ru.gdcn.tycoon.storage.repository.base.IPlayerResourceRepository
import java.util.*

class PlayerResourceRepositoryImpl :
    BaseDataRepository<PlayerResource>("PlayerResource"),
    IPlayerResourceRepository
{
    override fun findByCompositeId(session: Session, id: CompositeKeyPlayerResource): PlayerResource?
            = findByPrimaryKey(session, PlayerResource::class.java, id)

    override fun findByPlayerId(session: Session, playerId: Long): List<PlayerResource>?
            = findByColumnName(session,"player_id", playerId.toString())
}