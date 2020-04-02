package ru.gdcn.tycoon.storage.repository

import org.hibernate.Session
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ru.gdcn.tycoon.storage.entity.Player
import ru.gdcn.tycoon.storage.repository.base.BaseDataRepository
import ru.gdcn.tycoon.storage.repository.base.IPlayerRepository
import java.util.*

class PlayerRepositoryImpl : BaseDataRepository<Player>("Player"), IPlayerRepository {

    private val logger: Logger by lazy { LoggerFactory.getLogger(PlayerRepositoryImpl::class.java) }

    override fun save(session: Session, player: Player): Long? = saveEntity(session, player) as Long?

    override fun findByUserId(session: Session, userId: Long): Player? {
        val selectResult = findByColumnName(session, "userId", userId.toString())
        return if (selectResult == null) {
            null
        } else {
            if (selectResult.size > 1) {
                logger.error("Персонажей у пользователя с $userId больше одного!")
                null
            } else {
                selectResult.first()
            }
        }
    }

    override fun findByCityId(session: Session, cityId: Long): List<Player>?
            = findByColumnName(session,"cityId", cityId.toString())
}
