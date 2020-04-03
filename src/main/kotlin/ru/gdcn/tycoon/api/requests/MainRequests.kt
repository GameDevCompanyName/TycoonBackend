package ru.gdcn.tycoon.api.requests

import com.fasterxml.jackson.databind.ObjectMapper

import org.json.simple.JSONObject

import ru.gdcn.tycoon.api.FramesHandler
import ru.gdcn.tycoon.api.conf.Request
import ru.gdcn.tycoon.api.conf.Response
import ru.gdcn.tycoon.api.conf.ResponseCauseText
import ru.gdcn.tycoon.api.conf.ResponseStatus
import ru.gdcn.tycoon.api.entity.CityInfo
import ru.gdcn.tycoon.storage.StorageHelper
import ru.gdcn.tycoon.storage.TransactionResult
import ru.gdcn.tycoon.storage.entity.City
import ru.gdcn.tycoon.storage.entity.Player

import java.util.*


object MainRequests {

    private var worldMap: String? = null

    suspend fun execute(
        username: String,
        request: Request,
        actionListener: FramesHandler.RequestExecutorListener
    ) {
        val message = when (request.method) {
            RequestedResourceType.REQ_GAME_PLAYER.resource -> {
                val player = getPlayerInfo(username)
                if (player.isEmpty) {
                    Response(ResponseStatus.ERROR.code, ResponseCauseText.FAILED_GET_INFO.text).toJSONString()
                } else {
                    Response(ResponseStatus.OK.code, player.get()).toJSONString()
                }
            }
            RequestedResourceType.REQ_GAME_CITY.resource -> {
                val cityInfo = getCityInfo(username)
                if (cityInfo.isEmpty) {
                    Response(ResponseStatus.ERROR.code, ResponseCauseText.FAILED_GET_INFO.text).toJSONString()
                } else {
                    Response(ResponseStatus.OK.code, cityInfo.get()).toJSONString()
                }
            }
            RequestedResourceType.REQ_GAME_INIT.resource -> {
                val cityInfo = getCityInfo(username)
                val player = getPlayerInfo(username)
                if (worldMap == null) {
                    val tmp = getWorldMap()
                    if (!tmp.isEmpty) {
                        worldMap = tmp.get()
                    }
                }
                if (cityInfo.isEmpty || player.isEmpty || worldMap == null) {
                    Response(ResponseStatus.ERROR.code, ResponseCauseText.FAILED_GET_INFO.text).toJSONString()
                } else {

                    val obj = JSONObject()
                    obj["city"] = cityInfo.get()
                    obj["player"] = player.get()
                    obj["world"] = worldMap

                    Response(ResponseStatus.OK.code, obj).toJSONString()
                }
            }
            RequestedResourceType.REQ_MOVE_TO_OTHER_CITY.resource -> {
                val toCityId = request.parameters["cityId"]
                if (toCityId == null) {
                    Response(ResponseStatus.ERROR.code, ResponseCauseText.FAILED_MOVE.text).toJSONString()
                } else {
                    val player = getPlayerByUserName(username)
                    var currentCityInfo = getCityInfo(username)
                    val newCityInfo = getCityInfo(toCityId.toLong())

                    if (player.isEmpty || currentCityInfo.isEmpty || newCityInfo.isEmpty) {
                        Response(ResponseStatus.ERROR.code, ResponseCauseText.FAILED_MOVE.text).toJSONString()
                    } else {
                        player.get().cityId = toCityId.toLong()
                        val result = StorageHelper.transaction {
                            StorageHelper.playerRepository.update(it, player.get())
                            TransactionResult(isRollback = false, data = true)
                        }

                        if (!result.isEmpty && result.get()) {
                            currentCityInfo = getCityInfo(currentCityInfo.get().id)
                            if (currentCityInfo.isEmpty) {
                                Response(ResponseStatus.ERROR.code, ResponseCauseText.FAILED_MOVE.text).toJSONString()
                            } else {
                                val r = Response(ResponseStatus.OK.code, currentCityInfo.get()).toJSONString()
                                actionListener.onSendAll(username, r)
                                r
                            }
                        } else {
                            Response(ResponseStatus.ERROR.code, ResponseCauseText.FAILED_MOVE.text).toJSONString()
                        }
                    }
                }
            }
            else -> Response(ResponseStatus.ERROR.code, ResponseCauseText.UNKNOWN_REQUEST.text).toJSONString()
        }

        actionListener.onSendResponse(username, message)
    }

    private fun getWorldMap(): Optional<String> {
        val city = StorageHelper.transaction<List<City>> {
            val city = StorageHelper.cityRepository.findAll(it)
            return@transaction if (city == null || city.isEmpty()) {
                TransactionResult(true, null)
            } else {
                TransactionResult(false, city)
            }
        }

        return if (city.isEmpty) {
            Optional.empty()
        } else {
            Optional.of(ObjectMapper().writer().writeValueAsString(city.get()))
        }
    }

    private fun getPlayerInfo(username: String): Optional<Player> = getPlayerByUserName(username)

    private fun getCityInfo(username: String): Optional<CityInfo> {
        val player = getPlayerByUserName(username)
        if (player.isEmpty) {
            return Optional.empty()
        }
        return getCityInfo(player.get().cityId)
    }

    private fun getCityInfo(cityId: Long): Optional<CityInfo> {
        val city = StorageHelper.transaction<City> { session ->
            val city = StorageHelper.cityRepository.findById(session, cityId)
                ?: return@transaction TransactionResult(true, null)

            val players = StorageHelper.playerRepository.findByCityId(session, city.id)
                ?: return@transaction TransactionResult(true, null)

            city.players = players.map { it.name }.toMutableSet()

            return@transaction TransactionResult(false, city)
        }

        return if (city.isEmpty) {
            Optional.empty()
        } else {
            Optional.of(city.get().getInfoNotForDrawing())
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
