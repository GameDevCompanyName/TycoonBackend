package ru.gdcn.tycoon.storage.repository

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ru.gdcn.tycoon.storage.entity.Player
import ru.gdcn.tycoon.storage.repository.base.BaseDataRepository
import ru.gdcn.tycoon.storage.repository.base.IPlayerRepository
import java.util.*

class PlayerRepositoryImpl : BaseDataRepository<Player>("Player"), IPlayerRepository {

    private val logger: Logger by lazy { LoggerFactory.getLogger(PlayerRepositoryImpl::class.java) }

    override fun save(player: Player): Long = saveEntity(player)

    override fun findByUserId(userId: Long): Optional<Player> {
        val selectResult = findByColumnName("userId", userId.toString())
        return if (selectResult.isEmpty()) {
            Optional.empty()
        } else {
            if (selectResult.size > 1) {
                logger.error("Персонажей у пользователя с $userId больше одного!")
                Optional.empty()
            } else {
                Optional.of(selectResult.first())
            }
        }
    }

    override fun findByCityId(cityId: Long): List<Player> = findByColumnName("cityId", cityId.toString())
}
