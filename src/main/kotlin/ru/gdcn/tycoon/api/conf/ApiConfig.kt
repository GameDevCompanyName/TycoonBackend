package ru.gdcn.tycoon.api.conf

import com.fasterxml.jackson.databind.ObjectMapper
import org.json.simple.JSONObject

class Response<T>(val status: Int, val type: String, val data: T) {
    companion object {
        fun createErrorResponseJSONString(type: ResponseType, errorText: String): String {
            val tmpObj = JSONObject()
            tmpObj["cause"] = errorText

            return Response(
                ResponseStatus.ERROR.code,
                type.name,
                tmpObj
            ).toJSONString()
        }
    }

    fun toJSONString(): String = ObjectMapper().writer().writeValueAsString(this)
}
class Request(val method: String, val parameters: JSONObject)

enum class ResponseStatus(val code: Int) {
    OK(200),
    ERROR(400)
}

enum class ResponseType() {
    player,
    city,
    world,
    move,
    playerMoved,
    deal
}

enum class ResponseCauseText(val text: String) {
    LOGGED("Пользователь залогинился."),
    REGISTERED("Пользователь успешно зарегистрирован."),
    FAILED_CREATE_USER("Не удалось создать пользователя!"),
    FAILED_GET_INFO("Не удалось получить информацию!"),
    FAILED_GET_WORLD("Не удалось получить данные о мире!"),
    FAILED_MOVE("Не удалось переместиться!"),
    FAILED_CITY_NOT_EXIST("Такого города не существует"),
    FAILED_CITIES_NOT_NEIGHBORS("Города не являются соседями!"),
    FAILED_PLAYER_NOT_EXIST("Игрока c таким именем не существует!"),
    FAILED_PARAM("Неверные параметры запроса!"),
    FAILED_DEAL("Не удалось совершить сделку!"),
    FAILED_DEAL_PLAYER_HAVE_NO_RESOURCE("У игрока недостаточно ресурсов!"),
    FAILED_DEAL_CITY_HAVE_NO_RESOURCE("В городе недостаточно ресурсов!"),
    FAILED_DEAL_PLAYER_HAVE_NO_MONEY("У игрока недостаточно денег!"),
    UNKNOWN_REQUEST("Неизвестный запрос!")
}
