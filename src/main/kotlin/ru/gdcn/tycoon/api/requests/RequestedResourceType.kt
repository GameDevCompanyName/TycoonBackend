package ru.gdcn.tycoon.api.requests

enum class RequestedResourceType(val resource: String) {
    REQ_GAME_PLAYER("player"),
    REQ_GAME_CITY("city"),
    REQ_GAME_INIT("init"),
    REQ_MOVE_TO_OTHER_CITY("move")
}