package ru.gdcn.tycoon.api.requests

import org.json.simple.JSONArray

import org.json.simple.JSONObject

import ru.gdcn.tycoon.api.FramesHandler
import ru.gdcn.tycoon.api.conf.*
import ru.gdcn.tycoon.storage.StorageHelper
import ru.gdcn.tycoon.storage.TransactionResult
import ru.gdcn.tycoon.storage.entity.City
import ru.gdcn.tycoon.storage.entity.Player

import java.util.*


object MainRequests {

    private var worldMap: JSONArray? = null

    suspend fun execute(
        username: String,
        request: Request,
        actionListener: FramesHandler.RequestExecutorListener
    ) {
        val message = when (request.method) {
            RequestedResourceType.REQ_GAME_PLAYER.resource -> {
                val player = getPlayer(username)
                if (player.isEmpty) {
                    Response(
                        ResponseStatus.ERROR.code,
                        ResponseType.PLAYER.type,
                        ResponseCauseText.FAILED_GET_INFO.text
                    ).toJSONString()
                } else {
                    val jsonObject = player.get().toJSONObject(arrayOf(Player.FIELD_ALL))
                    Response(
                        ResponseStatus.OK.code,
                        ResponseType.PLAYER.type,
                        jsonObject
                    ).toJSONString()
                }
            }
            RequestedResourceType.REQ_GAME_CITY.resource -> {
                val city = getCity(username)
                if (city.isEmpty) {
                    Response(
                        ResponseStatus.ERROR.code,
                        ResponseType.CITY.type,
                        ResponseCauseText.FAILED_GET_INFO.text
                    ).toJSONString()
                } else {
                    val jsonObject = city.get().toJSONObject(City.FIELD_WITHOUT_GRAPHICS)
                    Response(
                        ResponseStatus.OK.code,
                        ResponseType.CITY.type,
                        jsonObject
                    ).toJSONString()
                }
            }
            RequestedResourceType.REQ_GAME_INIT.resource -> {
                val cityInfo = getCity(username)
                val player = getPlayer(username)
                if (worldMap == null) {
                    val tmp = getWorldMap()
                    if (!tmp.isEmpty) {
                        worldMap = tmp.get()
                    }
                }
                if (cityInfo.isEmpty || player.isEmpty || worldMap == null) {
                    Response(
                        ResponseStatus.ERROR.code,
                        ResponseType.WORLD.type,
                        ResponseCauseText.FAILED_GET_INFO.text
                    ).toJSONString()
                } else {

                    val obj = JSONObject()
                    obj["city"] = cityInfo.get().toJSONObject(City.FIELD_WITHOUT_GRAPHICS)
                    obj["player"] = player.get().toJSONObject(arrayOf(Player.FIELD_ALL))
                    obj["world"] = worldMap

                    Response(
                        ResponseStatus.OK.code,
                        ResponseType.WORLD.type,
                        obj
                    ).toJSONString()
                }
            }
            RequestedResourceType.REQ_MOVE_TO_OTHER_CITY.resource -> {
                val toCityId = request.parameters["cityId"]
                if (toCityId == null) {
                    Response(
                        ResponseStatus.ERROR.code,
                        ResponseType.MOVE.type,
                        ResponseCauseText.FAILED_MOVE.text
                    ).toJSONString()
                } else {
                    val player = getPlayer(username)
                    var currentCityInfo = getCity(username)
                    val newCityInfo = getCityById(toCityId.toLong())

                    if (player.isEmpty || currentCityInfo.isEmpty || newCityInfo.isEmpty) {
                        Response(
                            ResponseStatus.ERROR.code,
                            ResponseType.MOVE.type,
                            ResponseCauseText.FAILED_MOVE.text
                        ).toJSONString()
                    } else {
                        player.get().cityId = toCityId.toLong()
                        val result = StorageHelper.transaction {
                            StorageHelper.playerRepository.update(it, player.get())
                            TransactionResult(isRollback = false, data = true)
                        }

                        if (!result.isEmpty && result.get()) {
                            currentCityInfo = getCityById(currentCityInfo.get().id)
                            if (currentCityInfo.isEmpty) {
                                Response(
                                    ResponseStatus.ERROR.code,
                                    ResponseType.MOVE.type,
                                    ResponseCauseText.FAILED_MOVE.text
                                ).toJSONString()
                            } else {
                                val response = Response(
                                    ResponseStatus.OK.code,
                                    ResponseType.MOVE.type,
                                    currentCityInfo.get().toJSONObject(City.FIELD_WITHOUT_GRAPHICS)
                                ).toJSONString()
                                actionListener.onSendAll(username, response)
                                response
                            }
                        } else {
                            Response(
                                ResponseStatus.ERROR.code,
                                ResponseType.MOVE.type,
                                ResponseCauseText.FAILED_MOVE.text
                            ).toJSONString()
                        }
                    }
                }
            }
            else -> Response(
                ResponseStatus.ERROR.code,
                request.method,
                ResponseCauseText.UNKNOWN_REQUEST.text
            ).toJSONString()
        }

        actionListener.onSendResponse(username, message)
    }

    private fun getWorldMap(): Optional<JSONArray> {
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
            val a = JSONArray()
            a.addAll(
                city.get().map { it.toJSONObject(arrayOf(City.FIELD_ALL)) }
            )
            Optional.of(a)
        }
    }

    private fun getCity(username: String): Optional<City> {
        val player = getPlayer(username)
        if (player.isEmpty) {
            return Optional.empty()
        }
        return getCityById(player.get().cityId)
    }

    private fun getCityById(cityId: Long): Optional<City> {
        val city = StorageHelper.transaction<City> { session ->
            val city = StorageHelper.cityRepository.findById(session, cityId)
                ?: return@transaction TransactionResult(true, null)

            val players = StorageHelper.playerRepository.findByCityId(session, city.id)
                ?: return@transaction TransactionResult(true, null)

            val road = StorageHelper.roadRepository.findByCityId(session, city.id)
                ?: return@transaction TransactionResult(true, null)

            city.players = players.map { it.name }.toMutableSet()
            city.neighbors = road.map {
                if (city.id == it.compositeId.fromCityId) {
                    it.compositeId.toCityId
                } else {
                    it.compositeId.fromCityId
                }
            }.toMutableSet()

            return@transaction TransactionResult(false, city)
        }

        return if (city.isEmpty) {
            Optional.empty()
        } else {
            Optional.of(city.get())
        }
    }

    private fun getPlayer(username: String): Optional<Player> {
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
