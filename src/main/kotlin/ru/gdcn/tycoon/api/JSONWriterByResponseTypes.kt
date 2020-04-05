package ru.gdcn.tycoon.api

import org.json.simple.JSONArray
import org.json.simple.JSONObject
import ru.gdcn.tycoon.storage.entity.City
import ru.gdcn.tycoon.storage.entity.Player

object JSONWriterByResponseTypes {
    fun createObjectForCity(city: City): JSONObject {
        val cityObj = city.toJSONObject(
            arrayOf(
                City.FIELD_ID,
                City.FIELD_NAME,
                City.FIELD_POPULATION,
                City.FIELD_NEIGHBORS,
                City.FIELD_RESOURCES
            )
        )
        val arrayPlayer = JSONArray()
        arrayPlayer.addAll(
            city.players.map { it.name }
        )
        cityObj[City.FIELD_PLAYERS] = arrayPlayer

        return cityObj
    }

    fun createObjectForMove(city: City): JSONObject {
        val cityObj = city.toJSONObject(
            arrayOf(
                City.FIELD_ID,
                City.FIELD_NAME,
                City.FIELD_POPULATION,
                City.FIELD_NEIGHBORS,
                City.FIELD_RESOURCES
            )
        )
        val arrayPlayer = JSONArray()
        arrayPlayer.addAll(
            city.players.map { it.name }
        )
        cityObj[City.FIELD_PLAYERS] = arrayPlayer

        return cityObj
    }

    fun createObjectForWorld(city: City): JSONObject {
        val cityObj = city.toJSONObject(
            arrayOf(
                City.FIELD_ID,
                City.FIELD_COLOR,
                City.FIELD_POLYGON_DATA,
                City.FIELD_PLAYERS
            )
        )
        val arrayPlayer = JSONArray()
        arrayPlayer.addAll(
            city.players.map {
                it.toJSONObject(arrayOf(Player.FIELD_ID, Player.FIELD_NAME))
            }
        )
        cityObj[City.FIELD_PLAYERS] = arrayPlayer

        return cityObj
    }

    fun createObjectForPlayerMoved(playerId: Long, oldCityId: Long, newCityId: Long): JSONObject {
        val obj = JSONObject()
        obj["playerId"] = playerId
        obj["oldCityId"] = oldCityId
        obj["newCityId"] = newCityId

        return obj
    }

    fun createObjectForDeal(city: City, player: Player): JSONObject {
        val cityObj = createObjectForCity(city)
        val playerObj = player.toJSONObject(Player.FIELD_ALL)

        val obj = JSONObject()
        obj["city"] = cityObj
        obj["player"] = playerObj

        return obj
    }
}