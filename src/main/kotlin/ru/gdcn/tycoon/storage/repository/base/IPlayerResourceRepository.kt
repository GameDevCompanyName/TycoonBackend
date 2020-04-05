package ru.gdcn.tycoon.storage.repository.base

import org.hibernate.Session
import ru.gdcn.tycoon.storage.entity.CompositeKeyPlayerResource
import ru.gdcn.tycoon.storage.entity.PlayerResource
import java.util.*

interface IPlayerResourceRepository {
    fun findByCompositeId(session: Session, id: CompositeKeyPlayerResource): PlayerResource?
    fun findByPlayerId(session: Session, playerId: Long): List<PlayerResource>?
    fun saveOrUpdate(session: Session, playerResource: PlayerResource)
}