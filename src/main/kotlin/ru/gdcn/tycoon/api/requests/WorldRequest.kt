package ru.gdcn.tycoon.api.requests

import org.json.simple.JSONArray
import org.json.simple.JSONObject
import ru.gdcn.tycoon.api.FramesHandler
import ru.gdcn.tycoon.api.JSONWriterByResponseTypes
import ru.gdcn.tycoon.api.conf.*
import ru.gdcn.tycoon.storage.StorageHelper
import ru.gdcn.tycoon.storage.TransactionResult
import ru.gdcn.tycoon.storage.entity.City
import ru.gdcn.tycoon.storage.entity.Player

object WorldRequest : BaseRequest() {

    private var worldMap: JSONArray? = null

    override suspend fun execute(
        username: String,
        request: Request,
        actionListener: FramesHandler.RequestExecutorListener
    ) {
        val cityInfo = getCity(username)
        val player = getPlayer(username)

        if (cityInfo.isEmpty || player.isEmpty || getWorldMap() == null) {
            actionListener.onSendResponse(
                username,
                Response(
                    ResponseStatus.ERROR.code,
                    ResponseType.world.name,
                    ResponseCauseText.FAILED_GET_WORLD.text
                ).toJSONString()
            )
        } else {
            val obj = JSONObject()
            obj["city"] = JSONWriterByResponseTypes.createObjectForCity(cityInfo.get())
            obj["player"] = player.get().toJSONObject(Player.FIELD_ALL)
            obj["world"] = getWorldMap()

            actionListener.onSendResponse(
                username,
                Response(
                    ResponseStatus.OK.code,
                    ResponseType.world.name,
                    obj
                ).toJSONString()
            )
        }
    }

    private fun getWorldMap(): JSONArray? {
//        if (worldMap == null) {
            val city = StorageHelper.transaction<List<City>> { session ->
                val city = StorageHelper.cityRepository.findAll(session)
                    ?: return@transaction TransactionResult(true, null)

                city.forEach {
                    val players = StorageHelper.playerRepository.findByCityId(session, it.id)
                        ?: return@transaction TransactionResult(true, null)

                    it.players = players.toMutableSet()
                }

                return@transaction if (city.isEmpty()) {
                    TransactionResult(true, null)
                } else {
                    TransactionResult(false, city)
                }
            }

            if (!city.isEmpty) {
                val a = JSONArray()
                a.addAll(
                    city.get().map {
                        JSONWriterByResponseTypes.createObjectForWorld(it)
                    }
                )
                worldMap = a
            }
//        }

        return worldMap
    }
}