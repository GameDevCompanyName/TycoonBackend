package ru.gdcn.tycoon.api.conf

import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.auth.authenticate
import io.ktor.routing.Routing
import io.ktor.websocket.WebSockets
import io.ktor.websocket.webSocket

import ru.gdcn.tycoon.api.FramesHandler
import ru.gdcn.tycoon.auth.AUTH_FORM_NAME


fun Application.installWebSocket() {
    install(WebSockets)
}

fun Routing.routeWebSocket() {
    authenticate(AUTH_FORM_NAME) {
        webSocket("/game") {
            FramesHandler.handle(this)
        }
    }
}
