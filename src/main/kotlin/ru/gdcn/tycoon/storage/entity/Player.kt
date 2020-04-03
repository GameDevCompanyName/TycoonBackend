package ru.gdcn.tycoon.storage.entity

import com.fasterxml.jackson.annotation.JsonIgnore

import org.hibernate.validator.constraints.Range
import org.json.simple.JSONObject

import javax.persistence.*

@Entity
@Table(name = "t_player")
class Player() {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long = -1

    @Column(unique = true, nullable = false)
    lateinit var name: String

    @Range(min = 0)
    @Column(nullable = false)
    var money: Long = 0

    @Column(name = "city_id", nullable = false)
    @JoinColumn(name = "city_id")
    var cityId: Long = -1

    @Column(name = "user_id", unique = true, nullable = false)
    @JoinColumn(name = "user_id")
    var userId: Long = -1

    constructor(name: String, money: Long, cityId: Long, userId: Long) : this() {
        this.name = name
        this.money = money
        this.userId = userId
        this.cityId = cityId
    }

    @JsonIgnore
    fun toJSONObject(fields: Array<String>): JSONObject {
        val obj = JSONObject()

        if (fields.contains(FIELD_ALL)) {
            obj[FIELD_ID] = id
            obj[FIELD_NAME] = name
            obj[FIELD_MONEY] = money
            obj[FIELD_CITY_ID] = cityId
            obj[FIELD_USER_ID] = userId

            return obj
        }

        if (fields.contains(FIELD_ID)) {
            obj[FIELD_ID] = id
        }
        if (fields.contains(FIELD_NAME)) {
            obj[FIELD_NAME] = name
        }
        if (fields.contains(FIELD_MONEY)) {
            obj[FIELD_MONEY] = money
        }
        if (fields.contains(FIELD_CITY_ID)) {
            obj[FIELD_CITY_ID] = cityId
        }
        if (fields.contains(FIELD_USER_ID)) {
            obj[FIELD_USER_ID] = userId
        }

        return obj
    }

    companion object {
        const val FIELD_ID = "id"
        const val FIELD_NAME = "name"
        const val FIELD_MONEY = "money"
        const val FIELD_CITY_ID = "cityId"
        const val FIELD_USER_ID = "userId"
        const val FIELD_ALL = "*"
    }
}
