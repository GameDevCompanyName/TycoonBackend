package ru.gdcn.tycoon.api.requests

import ru.gdcn.tycoon.api.FramesHandler
import ru.gdcn.tycoon.api.conf.Request
import ru.gdcn.tycoon.storage.StorageHelper
import ru.gdcn.tycoon.storage.TransactionResult
import ru.gdcn.tycoon.storage.entity.City
import ru.gdcn.tycoon.storage.entity.Player

import java.util.*

abstract class BaseRequest {

    abstract suspend fun execute(username: String, request: Request, actionListener: FramesHandler.RequestExecutorListener)

    fun getCity(username: String): Optional<City> {
        val player = getPlayer(username)
        if (player.isEmpty) {
            return Optional.empty()
        }
        return getCityById(player.get().cityId)
    }

    fun getCityById(cityId: Long): Optional<City> {
        val city = StorageHelper.transaction<City> { session ->
            val city = StorageHelper.cityRepository.findById(session, cityId)
                ?: return@transaction TransactionResult(true, null)

            val players = StorageHelper.playerRepository.findByCityId(session, city.id)
                ?: return@transaction TransactionResult(true, null)

            val road = StorageHelper.roadRepository.findByCityId(session, city.id)
                ?: return@transaction TransactionResult(true, null)

            city.players = players.toMutableSet()
            city.neighbors = road.map {
                if (city.id == it.compositeId.fromCityId) {
                    it.compositeId.toCityId
                } else {
                    it.compositeId.fromCityId
                }
            }.toMutableSet()

            val cityResource = StorageHelper.cityResourceRepository.findByCityId(session, city.id)
                ?: return@transaction TransactionResult<City>(true, null)

            cityResource.forEach {
                val res = StorageHelper.resourceRepository.findById(session, it.compositeId.resourceId)
                    ?: return@transaction TransactionResult<City>(true, null)
                it.name = res.name
            }

            city.resources = cityResource.toMutableSet()

            return@transaction TransactionResult(false, city)
        }

        return if (city.isEmpty) {
            Optional.empty()
        } else {
            Optional.of(city.get())
        }
    }

    fun getPlayer(username: String): Optional<Player> {
        return StorageHelper.transaction { session ->
            val user = StorageHelper.userRepository.findByName(session, username)
            if (user == null) {
                return@transaction TransactionResult<Player>(true, null)
            } else {
                val player = StorageHelper.playerRepository.findByUserId(session, user.id)
                    ?: return@transaction TransactionResult<Player>(true, null)

                val playerResource = StorageHelper.playerResourceRepository.findByPlayerId(session, player.id)
                    ?: return@transaction TransactionResult<Player>(true, null)

                playerResource.forEach {
                    val res = StorageHelper.resourceRepository.findById(session, it.compositeId.resourceId)
                        ?: return@transaction TransactionResult<Player>(true, null)
                    it.name = res.name
                }

                player.resources = playerResource.toMutableSet()

                return@transaction TransactionResult(false, player)
            }
        }
    }
}
