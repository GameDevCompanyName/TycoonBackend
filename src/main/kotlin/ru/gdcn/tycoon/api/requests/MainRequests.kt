package ru.gdcn.tycoon.api.requests

import com.fasterxml.jackson.databind.ObjectMapper

import ru.gdcn.tycoon.api.FramesHandler
import ru.gdcn.tycoon.api.conf.Request
import ru.gdcn.tycoon.api.conf.Response
import ru.gdcn.tycoon.api.conf.ResponseStatus
import ru.gdcn.tycoon.storage.StorageHelper
import ru.gdcn.tycoon.storage.TransactionResult
import ru.gdcn.tycoon.storage.entity.Player
import java.util.*

object MainRequests {
    suspend fun execute(
        username: String,
        request: Request,
        actionListener: FramesHandler.RequestExecutorListener
    ) {
        val response = when (request.method) {
            RequestedResourceType.REQ_GAME_PLAYER.resource -> getPlayerInfo(username)
            RequestedResourceType.REQ_GAME_CITY.resource -> getCityInfo(username)
            else -> Response(ResponseStatus.ERROR.code, "")
        }

        actionListener.onSendResponse(username, response)
    }

    private fun getPlayerInfo(username: String): Response<String> {
        val player = getPlayerByUserName(username)
        if (player.isEmpty) {
            return Response(ResponseStatus.ERROR.code, "")
        }

        val stringPlayer = ObjectMapper().writer().writeValueAsString(player.get())
        return Response(ResponseStatus.OK.code, stringPlayer)
    }

    private fun getCityInfo(username: String): Response<String> {
        val player = getPlayerByUserName(username)
        if (player.isEmpty) {
            return Response(ResponseStatus.ERROR.code, "")
        }

        val stringCity = StorageHelper.transaction<String> { session ->
            val city = StorageHelper.cityRepository.findById(session, player.get().cityId)
            if (city == null) {
                return@transaction TransactionResult(
                    true,
                    null
                )
            }

            val players = StorageHelper.playerRepository.findByCityId(session, city.id)
            if (players == null) {
                return@transaction TransactionResult(
                    true,
                    null
                )
            }

            city.players = players.map { it.name }.toMutableSet()

            return@transaction TransactionResult(
                false,
                ObjectMapper().writer().writeValueAsString(city)
            )
        }

        return if (stringCity.isEmpty) {
            Response(ResponseStatus.ERROR.code, "Сouldn't get information about the city!")
        } else {
            Response(ResponseStatus.OK.code, stringCity.get())
        }
    }

    private fun getPlayerByUserName(username: String): Optional<Player> {
        return StorageHelper.transaction { session ->
            val user = StorageHelper.userRepository.findByName(session, username)
            if (user == null) {
                return@transaction TransactionResult<Player>(true, null)
            } else {
                val player = StorageHelper.playerRepository.findByUserId(session, user.id)
                return@transaction if (player == null) {
                    TransactionResult<Player>(true, null)
                } else {
                    TransactionResult(false, player)
                }
            }
        }
    }
}
