package ru.gdcn.tycoon.api

import com.fasterxml.jackson.databind.ObjectMapper
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readText
import io.ktor.sessions.get
import io.ktor.sessions.sessions
import io.ktor.websocket.DefaultWebSocketServerSession

import ru.gdcn.tycoon.api.conf.Request

import ru.gdcn.tycoon.api.requests.MainRequests
import ru.gdcn.tycoon.api.requests.RequestedResourceType
import ru.gdcn.tycoon.auth.SessionToken

import java.util.concurrent.ConcurrentHashMap


object FramesHandler {

    interface RequestExecutorListener {
        suspend fun onSendResponse(sender: String, jsonMessage: String)
        suspend fun onDisconnect(username: String, webSocket: DefaultWebSocketServerSession)
        suspend fun onSendAll(username: String, message: String)
    }

    private val executorListener = object : RequestExecutorListener {
        override suspend fun onSendResponse(sender: String, jsonMessage: String) {
            val webSocket = connections[sender] ?: return
            webSocket.outgoing.send(Frame.Text(jsonMessage))
        }

        override suspend fun onSendAll(username: String, message: String) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override suspend fun onDisconnect(username: String, webSocket: DefaultWebSocketServerSession) {
            connections.remove(username)
        }
    }

    private val connections = ConcurrentHashMap<String, DefaultWebSocketServerSession>()

    suspend fun handle(webSocket: DefaultWebSocketServerSession) {
        val username = webSocket.call.sessions.get<SessionToken>()?.username ?: return
        connections[username] = webSocket
        loop@ for (frame in webSocket.incoming) {
            when (frame) {
                is Frame.Text -> {
                    executeRequestByClient(username, frame.readText())
                }
                else -> {
                    continue@loop
                }
            }
        }
    }

    private suspend fun executeRequestByClient(username: String, message: String) {
        try {
            val request = ObjectMapper().reader().readValue<Request>(message)
            when (request.method) {
                RequestedResourceType.REQ_GAME_PLAYER.resource,
                RequestedResourceType.REQ_GAME_CITY.resource -> MainRequests.execute(
                    username,
                    request,
                    executorListener
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
