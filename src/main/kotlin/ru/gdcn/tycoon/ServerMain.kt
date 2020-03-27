package ru.gdcn.tycoon

import io.ktor.application.*
import io.ktor.features.CORS
import io.ktor.features.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.jackson.JacksonConverter
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

import ru.gdcn.tycoon.auth.installAuth
import ru.gdcn.tycoon.auth.installSession
import ru.gdcn.tycoon.auth.routeAuth

fun main() {
    val host = JSONHelper.env[JSONHelper.KEY_SERVER_HOST] ?: throw IllegalStateException("HOST not found!")
    val port = JSONHelper.env[JSONHelper.KEY_SERVER_PORT]?.toInt() ?: throw IllegalStateException("PORT not found!")
    embeddedServer(Netty, host = host, port = port) {
        installCORS()
        installAuth()
        installSession()
        installRouting()
        install(ContentNegotiation) {
            register(ContentType.Application.Json, JacksonConverter())
        }
    }.start()
}

fun Application.installRouting() {
    routing {
        routeAuth()
    }
}

fun Application.installCORS() {
    install(CORS) {
        header(HttpHeaders.AccessControlAllowOrigin)
        anyHost()
    }
}
