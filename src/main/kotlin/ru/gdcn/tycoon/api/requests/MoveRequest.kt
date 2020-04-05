package ru.gdcn.tycoon.api.requests

import org.json.simple.JSONObject

import ru.gdcn.tycoon.api.FramesHandler
import ru.gdcn.tycoon.api.JSONWriterByResponseTypes
import ru.gdcn.tycoon.api.conf.*
import ru.gdcn.tycoon.storage.StorageHelper
import ru.gdcn.tycoon.storage.TransactionResult
import ru.gdcn.tycoon.storage.entity.City
import ru.gdcn.tycoon.storage.entity.Player

import java.util.*

object MoveRequest : BaseRequest() {

    override suspend fun execute(
        username: String,
        request: Request,
        actionListener: FramesHandler.RequestExecutorListener
    ) {
        val responseMessage: String

        val toCityId = request.parameters["cityId"]
        if (toCityId == null || toCityId !is Long) {
            responseMessage = Response.createErrorResponseJSONString(
                ResponseType.move,
                ResponseCauseText.FAILED_MOVE.text
            )
            actionListener.onSendResponse(username, responseMessage)
            return
        }

        val player = getPlayer(username)
        if (player.isEmpty) {
            responseMessage = Response.createErrorResponseJSONString(
                ResponseType.move,
                ResponseCauseText.FAILED_PLAYER_NOT_EXIST.text
            )
            actionListener.onSendResponse(username, responseMessage)
            return
        }

        val tempNewCityInfo = getCityById(toCityId)
        if (tempNewCityInfo.isEmpty) {
            responseMessage = Response.createErrorResponseJSONString(
                ResponseType.move,
                ResponseCauseText.FAILED_CITY_NOT_EXIST.text
            )
            actionListener.onSendResponse(username, responseMessage)
            return
        }

        val checkNeighborsText = checkNeighbors(player.get().cityId, toCityId)
        if (checkNeighborsText != null) {
            responseMessage = Response.createErrorResponseJSONString(
                ResponseType.move,
                checkNeighborsText
            )
            actionListener.onSendResponse(username, responseMessage)
            return
        }

        val oldCityId = player.get().cityId
        val newCityInfo = movePlayer(player.get(), toCityId)
        if (newCityInfo.isPresent) {
            val currentCityInfo = getCityById(oldCityId)
            if (!currentCityInfo.isEmpty) {
                actionListener.onSendEventTo(
                    currentCityInfo.get().players.map { it.name },
                    createOkResponseJSONStringForCity(currentCityInfo.get())
                )
            }

            actionListener.onSendResponse(
                username,
                createOkResponseJSONStringForMove(newCityInfo.get())
            )

            actionListener.onSendEventTo(
                newCityInfo.get().players.map { it.name },
                createOkResponseJSONStringForCity(newCityInfo.get())
            )

            actionListener.onSendAll(
                username,
                createOkResponseJSONStringForPlayerMoved(player.get().id, oldCityId, toCityId)
            )
        } else {
            responseMessage = Response.createErrorResponseJSONString(
                ResponseType.move,
                ResponseCauseText.FAILED_MOVE.text
            )
            actionListener.onSendResponse(username, responseMessage)
        }
    }

    private fun createOkResponseJSONStringForCity(cityInfo: City): String {
        val objResponse = JSONObject()
        objResponse["city"] = JSONWriterByResponseTypes.createObjectForCity(cityInfo)

        return Response(
            ResponseStatus.OK.code,
            ResponseType.city.name,
            objResponse
        ).toJSONString()
    }

    private fun createOkResponseJSONStringForMove(cityInfo: City): String {
        val objResponse = JSONObject()
        objResponse["city"] = JSONWriterByResponseTypes.createObjectForMove(cityInfo)

        return Response(
            ResponseStatus.OK.code,
            ResponseType.move.name,
            objResponse
        ).toJSONString()
    }

    private fun createOkResponseJSONStringForPlayerMoved(playerId: Long, oldCityId: Long, newCityId: Long): String {
        val objResponse = JSONObject()
        objResponse["moveInfo"] = JSONWriterByResponseTypes.createObjectForPlayerMoved(playerId, oldCityId, newCityId)

        return Response(
            ResponseStatus.OK.code,
            ResponseType.playerMoved.name,
            objResponse
        ).toJSONString()
    }

    private fun movePlayer(player: Player, toCityId: Long): Optional<City> {
        player.cityId = toCityId
        return StorageHelper.transaction {
            StorageHelper.playerRepository.update(it, player)

            val city = getCityById(toCityId)
            if (city.isEmpty) {
                return@transaction TransactionResult<City>(true, null)
            }
            city.get().players.add(player)

            TransactionResult(isRollback = false, data = city.get())
        }
    }

    private fun checkNeighbors(fromCityId: Long, toCityId: Long): String? {
        val currentCityInfo = getCityById(fromCityId)
        return if (currentCityInfo.isEmpty) {
            ResponseCauseText.FAILED_MOVE.text
        } else if (!currentCityInfo.get().neighbors.contains(toCityId)) {
            ResponseCauseText.FAILED_CITIES_NOT_NEIGHBORS.text
        } else {
            null
        }
    }
}
