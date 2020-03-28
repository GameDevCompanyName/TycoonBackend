package ru.gdcn.tycoon.api.websocket

import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.http.cio.websocket.CloseReason
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.close
import io.ktor.http.cio.websocket.readText
import io.ktor.routing.Routing
import io.ktor.sessions.SessionTransportTransformerEncrypt
import io.ktor.sessions.get
import io.ktor.sessions.sessions
import io.ktor.websocket.WebSockets
import io.ktor.websocket.webSocket
import ru.gdcn.tycoon.auth.SessionToken

fun Application.installWebSocket() {
    install(WebSockets)
}

fun Routing.routeWebSocket() {
    webSocket("/game") {
        val a = "test"
        for (frame in incoming) {
            when (frame) {
                is Frame.Text -> {
                    val text = frame.readText()
                    println(text)
                    outgoing.send(Frame.Text("YOU SAID: ${call.sessions.get<SessionToken>()?.username}"))
                    if (text.equals("bye", ignoreCase = true)) {
                        close(CloseReason(CloseReason.Codes.NORMAL, "Client said BYE"))
                    }
                }
            }
        }
    }
}
