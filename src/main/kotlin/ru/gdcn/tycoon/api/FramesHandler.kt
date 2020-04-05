package ru.gdcn.tycoon.api

import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readText
import io.ktor.sessions.get
import io.ktor.sessions.sessions
import io.ktor.websocket.DefaultWebSocketServerSession

import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.isActive

import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser

import ru.gdcn.tycoon.api.conf.Request
import ru.gdcn.tycoon.api.conf.Response
import ru.gdcn.tycoon.api.conf.ResponseCauseText
import ru.gdcn.tycoon.api.conf.ResponseStatus
import ru.gdcn.tycoon.api.requests.*
import ru.gdcn.tycoon.auth.SessionToken

import java.util.concurrent.ConcurrentHashMap


object FramesHandler {

    interface RequestExecutorListener {
        suspend fun onSendResponse(recipient: String, jsonMessage: String)
        suspend fun onSendEventTo(recipients: List<String>, jsonMessage: String)
        suspend fun onDisconnect(username: String, webSocket: DefaultWebSocketServerSession)
        suspend fun onSendAll(excludeSelf: String?, jsonMessage: String)
        suspend fun getOnlineUser(): List<String>
    }

    private val executorListener = object : RequestExecutorListener {
        override suspend fun onSendResponse(recipient: String, jsonMessage: String) {
            val webSocket = connections[recipient] ?: return
            if (webSocket.isActive) {
                webSocket.outgoing.send(Frame.Text(jsonMessage))
            } else {
                connections.remove(recipient)
            }
        }

        override suspend fun onSendEventTo(recipients: List<String>, jsonMessage: String) {
            recipients.forEach {
                onSendResponse(it, jsonMessage)
            }
        }

        override suspend fun onSendAll(excludeSelf: String?, jsonMessage: String) {
            connections.forEach {
                if (excludeSelf == null || it.key != excludeSelf) {
                    it.value.outgoing.send(Frame.Text(jsonMessage))
                }
            }
        }

        override suspend fun onDisconnect(username: String, webSocket: DefaultWebSocketServerSession) {
            connections.remove(username)
        }

        override suspend fun getOnlineUser(): List<String> {
            return connections.map { it.key }
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
            connections.remove(username)
        } catch (e: Throwable) {
            println("onError!!!!!!!!!1")
            connections.remove(username)
            e.printStackTrace()
        }
    }

    private suspend fun executeRequestByClient(username: String, message: String) {
        try {
            val obj = JSONParser().parse(message) as JSONObject
            val method = obj["request"] as String
            val param = obj["parameters"] as JSONObject
            val request = Request(method, param)
            when (request.method) {
                RequestType.REQ_GAME_CITY.resource -> {
                    CityRequest.execute(
                        username,
                        request,
                        executorListener
                    )
                }
                RequestType.REQ_GAME_INIT.resource -> {
                    WorldRequest.execute(username, request, executorListener)
                }
                RequestType.REQ_GAME_MOVE_TO_OTHER_CITY.resource -> {
                    MoveRequest.execute(username, request, executorListener)
                }
                RequestType.REQ_GAME_PLAYER.resource -> {
                    PlayerRequest.execute(username, request, executorListener)
                }
                RequestType.REQ_GAME_DEAL.resource -> {
                    DealRequest.execute(username, request, executorListener)
                }
                else -> {
                    executorListener.onSendResponse(
                        username,
                        Response(
                            ResponseStatus.ERROR.code,
                            request.method,
                            ResponseCauseText.UNKNOWN_REQUEST.text
                        ).toJSONString()
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
