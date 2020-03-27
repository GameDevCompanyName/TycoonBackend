package ru.gdcn.tycoon.api

class Response<T>(val status: ResponseStatus, val entity: T)

enum class ResponseStatus(code: Int) {
    OK(200),
    ALREADY_LOGGED(300),
    ERROR(400)
}