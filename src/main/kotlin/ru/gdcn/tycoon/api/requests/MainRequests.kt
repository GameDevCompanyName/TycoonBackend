package ru.gdcn.tycoon.api.requests

import com.fasterxml.jackson.databind.ObjectMapper

import ru.gdcn.tycoon.api.FramesHandler
import ru.gdcn.tycoon.api.conf.Request
import ru.gdcn.tycoon.api.conf.Response
import ru.gdcn.tycoon.api.conf.ResponseStatus
import ru.gdcn.tycoon.storage.StorageHelper
import ru.gdcn.tycoon.storage.entity.Player
import java.util.*

object MainRequests {
    suspend fun execute(
        username: String,
        request: Request,
        actionListener: FramesHandler.RequestExecutorListener
    ) {
        val response = when(request.method) {
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

        val city = StorageHelper.cityRepository.findById(player.get().cityId)
        if (city.isEmpty) {
            return Response(ResponseStatus.ERROR.code, "")
        }

        val players = StorageHelper.playerRepository.findByCityId(city.get().id)
        city.get().players = players.map { it.name }.toMutableSet()

        val stringCity = ObjectMapper().writer().writeValueAsString(city.get())
        return Response(ResponseStatus.OK.code, stringCity)
    }

    private fun getPlayerByUserName(username: String): Optional<Player> {
        val user = StorageHelper.userRepository.findByName(username)
        if (user.isEmpty) {
            return Optional.empty()
        }

        return StorageHelper.playerRepository.findByUserId(user.get().id)
    }
}
