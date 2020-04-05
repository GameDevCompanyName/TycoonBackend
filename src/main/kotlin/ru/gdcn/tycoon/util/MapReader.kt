package ru.gdcn.tycoon.util

import com.fasterxml.jackson.databind.ObjectMapper
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import org.postgresql.core.Oid.JSON
import ru.gdcn.tycoon.storage.StorageHelper
import ru.gdcn.tycoon.storage.TransactionResult
import ru.gdcn.tycoon.storage.entity.*
import java.io.File
import java.io.FileReader

object MapReader {
    fun readMap() {
        try {
            val parser = JSONParser()
            val obj = parser.parse(FileReader("world 1488.json")) as JSONObject
            (obj["cities"] as JSONArray).map {
                val cityObj = it as JSONObject
                val city = City()
                city.id = cityObj["id"] as Long
                city.name = cityObj["name"] as String
                city.color = cityObj["color"] as Long
                city.population = cityObj["population"] as Long
                city.polygonData = cityObj["polygonData"] as String
                StorageHelper.transaction<Nothing> { session ->
                    val id = StorageHelper.cityRepository.save(session, city)
                    return@transaction if (id == null) {
                        TransactionResult(true)
                    } else {
                        TransactionResult(false)
                    }
                }
            }

            (obj["resources"] as JSONArray).map {
                val resObj = it as JSONObject
                val res = Resource()
                res.id = resObj["id"] as Long
                res.name = resObj["name"] as String
                StorageHelper.transaction<Nothing> { session ->
                    return@transaction if (StorageHelper.resourceRepository.save(session, res) == null) {
                        TransactionResult(true)
                    } else {
                        TransactionResult(false)
                    }
                }
            }

            (obj["city_resource"] as JSONArray).map {
                val crObj = it as JSONObject
                val cr = CityResource()
                val id = CompositeKeyCityResource(crObj["cityId"] as Long, crObj["resourceId"] as Long)
                cr.compositeId = id
                cr.cost = crObj["cost"] as Long
                cr.quantity = crObj["quantity"] as Long
                StorageHelper.transaction<Nothing> { session ->
                    return@transaction if (StorageHelper.cityResourceRepository.save(session, cr) == null) {
                        TransactionResult(true)
                    } else {
                        TransactionResult(false)
                    }
                }
            }

            (obj["roads"] as JSONArray).map {
                val crObj = it as JSONObject
                val cr = Road()
                val id = CompositeKeyRoad(crObj["from"] as Long, crObj["to"] as Long)
                cr.compositeId = id
                StorageHelper.transaction<Nothing> { session ->
                    return@transaction if (StorageHelper.roadRepository.save(session, cr) == null) {
                        TransactionResult(true)
                    } else {
                        TransactionResult(false)
                    }
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}