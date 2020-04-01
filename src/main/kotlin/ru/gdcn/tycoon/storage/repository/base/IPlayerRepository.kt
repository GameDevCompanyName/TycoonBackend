package ru.gdcn.tycoon.storage.repository.base

import ru.gdcn.tycoon.storage.entity.Player
import java.util.*

interface IPlayerRepository {
    fun save(player: Player): Long
    fun findByUserId(userId: Long): Optional<Player>
    fun findByCityId(cityId: Long): List<Player>
}