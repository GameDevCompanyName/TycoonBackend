package ru.gdcn.tycoon.storage.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.Session
import org.hibernate.validator.constraints.Range
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import javax.persistence.*
import kotlin.jvm.Transient

@Entity
@Table(name = "t_city")
class City {
    @Id
    var id: Long = -1

    @Column(unique = true, nullable = false)
    lateinit var name: String

    @Column(nullable = false)
    @Range(min = 0, max = 16_777_215)
    var color: Long = -1

    @Column(nullable = false)
    @Range(min = 0)
    var population: Long = 0

    @Column(name = "polygon_data", nullable = false, length = 1000)
    lateinit var polygonData: String

    @Transient
    var players: MutableSet<Player> = mutableSetOf()

    @Transient
    var neighbors: MutableSet<Long> = mutableSetOf()

    @Transient
    var resources: MutableSet<CityResource> = mutableSetOf()

    @JsonIgnore
    fun toJSONObject(fields: Array<String>): JSONObject {
        val obj = JSONObject()

        if (fields.contains(FIELD_ID)) {
            obj[FIELD_ID] = id
        }
        if (fields.contains(FIELD_NAME)) {
            obj[FIELD_NAME] = name
        }
        if (fields.contains(FIELD_COLOR)) {
            obj[FIELD_COLOR] = color
        }
        if (fields.contains(FIELD_POPULATION)) {
            obj[FIELD_POPULATION] = population
        }
        if (fields.contains(FIELD_POLYGON_DATA)) {
            obj[FIELD_POLYGON_DATA] = JSONParser().parse(polygonData) as JSONObject
        }
        if (fields.contains(FIELD_PLAYERS)) {
            val a = JSONArray()
            a.addAll(players.map {
                it.toJSONObject(Player.FIELD_ALL)
            })
            obj[FIELD_PLAYERS] = a
        }
        if (fields.contains(FIELD_NEIGHBORS)) {
            val a = JSONArray()
            a.addAll(neighbors)
            obj[FIELD_NEIGHBORS] = a
        }
        if (fields.contains(FIELD_RESOURCES)) {
            val a = JSONArray()
            a.addAll(
                resources.map {
                    val tmpObj = JSONObject()
                    tmpObj["id"] = it.compositeId.resourceId
                    tmpObj["name"] = it.name
                    tmpObj["cost"] = it.cost
                    tmpObj["quantity"] = it.quantity
                    tmpObj
                }
            )
            obj[FIELD_RESOURCES] = a
        }

        return obj
    }

    companion object {
        const val FIELD_ID = "id"
        const val FIELD_NAME = "name"
        const val FIELD_COLOR = "color"
        const val FIELD_POPULATION = "population"
        const val FIELD_POLYGON_DATA = "polygonData"
        const val FIELD_PLAYERS = "players"
        const val FIELD_NEIGHBORS = "neighbors"
        const val FIELD_RESOURCES = "resources"
        val FIELD_ALL = arrayOf(
            FIELD_ID,
            FIELD_NAME,
            FIELD_COLOR,
            FIELD_POPULATION,
            FIELD_POLYGON_DATA,
            FIELD_PLAYERS,
            FIELD_NEIGHBORS,
            FIELD_RESOURCES
        )
    }
}
