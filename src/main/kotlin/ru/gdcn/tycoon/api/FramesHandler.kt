package ru.gdcn.tycoon.api

import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readText
import io.ktor.sessions.get
import io.ktor.sessions.sessions
import io.ktor.websocket.DefaultWebSocketServerSession

import kotlinx.coroutines.channels.ClosedReceiveChannelException

import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser

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
//        val username = "alex"
        connections[username] = webSocket
        try {
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
        } catch (e: ClosedReceiveChannelException) {
            println("onClose!!!!!!!!!!!!!!1")
        } catch (e: Throwable) {
            println("onError!!!!!!!!!1")
            e.printStackTrace()
        }
    }

    private suspend fun executeRequestByClient(username: String, message: String) {
        try {
//            val request = ObjectMapper().reader().readValue<Request>(message)
            val obj = JSONParser().parse(message) as JSONObject
            val method = obj["method"] as String
            val param = obj["parameters"] as Map<String, String>
            val request = Request(method, param)
            when (request.method) {
                RequestedResourceType.REQ_GAME_PLAYER.resource,
                RequestedResourceType.REQ_GAME_CITY.resource,
                RequestedResourceType.REQ_GAME_INIT.resource,
                RequestedResourceType.REQ_MOVE_TO_OTHER_CITY.resource -> MainRequests.execute(
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
