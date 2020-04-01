package ru.gdcn.tycoon.api.conf

class Response<T>(val status: Int, val entity: T)
class Request(val method: String, val parameters: Map<String, String>)

enum class ResponseStatus(val code: Int) {
    OK(200),
    ALREADY_LOGGED(300),
    ERROR(400),
    FAILED_CREATE_PLAYER(403)
}