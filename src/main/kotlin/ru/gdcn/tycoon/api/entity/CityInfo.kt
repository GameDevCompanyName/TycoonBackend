package ru.gdcn.tycoon.api.entity

import com.fasterxml.jackson.databind.ObjectMapper

data class CityInfo(
    val id: Long,
    val name: String,
    val population: Int,
    val players: List<String>
) {
    fun toJSONString(): String = ObjectMapper().writer().writeValueAsString(this)
}