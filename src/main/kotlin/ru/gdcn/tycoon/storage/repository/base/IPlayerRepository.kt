package ru.gdcn.tycoon.storage.repository.base

import org.hibernate.Session
import ru.gdcn.tycoon.storage.entity.Player
import java.util.*

interface IPlayerRepository {
    fun save(session: Session, player: Player): Long?
    fun update(session: Session, player: Player)
    fun findByUserId(session: Session, userId: Long): Player?
    fun findByCityId(session: Session, cityId: Long): List<Player>?
}