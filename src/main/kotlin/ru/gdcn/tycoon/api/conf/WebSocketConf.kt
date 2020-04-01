package ru.gdcn.tycoon.api.conf

import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.routing.Routing
import io.ktor.websocket.WebSockets
import io.ktor.websocket.webSocket

import ru.gdcn.tycoon.api.FramesHandler


fun Application.installWebSocket() {
    install(WebSockets)
}

fun Routing.routeWebSocket() {
    webSocket("/game") {
        FramesHandler.handle(this)
    }
}
