package ru.gdcn.tycoon.storage.repository.base

import ru.gdcn.tycoon.storage.entity.CompositeKeyPlayerResource
import ru.gdcn.tycoon.storage.entity.PlayerResource
import java.util.*

interface IPlayerResourceRepository {
    fun findByCompositeId(id: CompositeKeyPlayerResource): Optional<PlayerResource>
    fun findByPlayerId(playerId: Long): List<PlayerResource>
}