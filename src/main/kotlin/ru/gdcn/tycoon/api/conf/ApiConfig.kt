package ru.gdcn.tycoon.api.conf

import com.fasterxml.jackson.databind.ObjectMapper
import org.json.simple.JSONObject

class Response<T>(val status: Int, val type: String, val data: T) {
    fun toJSONString(): String = ObjectMapper().writer().writeValueAsString(this)
}
class Request(val method: String, val parameters: JSONObject)

enum class ResponseStatus(val code: Int) {
    OK(200),
    ERROR(400)
}

enum class ResponseCauseText(val text: String) {
    LOGGED("Logged"),
    REGISTERED("Registered"),
    FAILED_CREATE_USER("Failed create user!"),
    FAILED_GET_INFO("Failed to get information!"),
    FAILED_MOVE("Failed move to other city!"),
    UNKNOWN_REQUEST("Unknown request!")
}
