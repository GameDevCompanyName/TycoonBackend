package ru.gdcn.tycoon.auth

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.*
import io.ktor.http.Parameters
import io.ktor.request.receiveParameters
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.sessions.*
import ru.gdcn.tycoon.api.Response
import ru.gdcn.tycoon.api.ResponseStatus

import ru.gdcn.tycoon.storage.StorageHelper
import ru.gdcn.tycoon.storage.entity.Role
import ru.gdcn.tycoon.storage.entity.User

class SessionToken(val username: String)

private const val PARAM_NAME_LOGIN_USERNAME = "username"
private const val PARAM_NAME_LOGIN_PASSWORD = "password"
private const val PARAM_NAME_REGISTRATION_PASSWORD_CONFIRM = "password"
private const val TOKEN_NAME = "TOKEN"

private const val AUTH_FROM_NAME = "FormAuth"

fun Application.installSession() {
    install(Sessions) {
        cookie<SessionToken>(TOKEN_NAME, SessionStorageMemory())
    }
}

fun Application.installAuth() {
    install(Authentication) {
        form(AUTH_FROM_NAME) {
            userParamName = PARAM_NAME_LOGIN_USERNAME
            passwordParamName = PARAM_NAME_LOGIN_PASSWORD
            challenge {
                call.respond(Response(ResponseStatus.ERROR, "Incorrect FORM parameter!"))
            }
            validate { credentials ->
                val user = StorageHelper.userRepository.findByName(credentials.name)
                if (!user.isEmpty && user.get().password == credentials.password) {
                    UserIdPrincipal(credentials.name)
                } else {
                    null
                }
            }
            skipWhen { call -> call.sessions.get<SessionToken>() != null }
        }
    }
}

fun Routing.routeAuth() {
    post("/registration") {
        if (call.sessions.get<SessionToken>() != null) {
            call.respond(Response(ResponseStatus.ALREADY_LOGGED, "User already logged!"))
            return@post
        }

        val parameters: Parameters
        try {
            parameters = call.receiveParameters()
        } catch (e: Exception) {
            e.printStackTrace()
            call.respond(Response(ResponseStatus.ERROR, "Can't receive parameters!"))
            return@post
        }

        val username = parameters[PARAM_NAME_LOGIN_USERNAME]
        val password = parameters[PARAM_NAME_LOGIN_PASSWORD]
        val passwordConfirm = parameters[PARAM_NAME_REGISTRATION_PASSWORD_CONFIRM]
        if (username == null || password == null || passwordConfirm == null) {
            call.respond(
                Response(
                    ResponseStatus.ERROR,
                    "Incorrect username=$username or password=$password or passwordConfirm=$passwordConfirm"
                )
            )
            return@post
        }

        val user = StorageHelper.userRepository.findByName(username)
        if (!user.isEmpty) {
            call.respond(Response(ResponseStatus.ERROR, "User already exists!"))
            return@post
        }

        if (password != passwordConfirm) {
            call.respond(Response(ResponseStatus.ERROR, "Password not equals!"))
            return@post
        }

        val newUser = User()
        newUser.username = username
        newUser.password = password
        newUser.role = Role.USER.id
        val status = StorageHelper.userRepository.save(newUser)
        if (!status) {
            call.respond(Response(ResponseStatus.ERROR, "Failed to create a user!"))
            return@post
        }

        call.sessions.set(SessionToken(newUser.username))
        call.respond(Response(ResponseStatus.OK, null))
    }
    authenticate(AUTH_FROM_NAME) {
        post("/login") {
            if (call.sessions.get<SessionToken>() != null) {
                call.respond(Response(ResponseStatus.ALREADY_LOGGED, "User already logged!"))
                return@post
            }

            val principal = call.principal<UserIdPrincipal>()
            if (principal == null) {
                call.respond(Response(ResponseStatus.ERROR, "Failed login!"))
                return@post
            }

            call.sessions.set(SessionToken(principal.name))
            call.respond(Response(ResponseStatus.OK, null))
        }
        get("/test") {
            call.respond(call.sessions.get<SessionToken>()?.username ?: "LOX")
        }
    }
}
