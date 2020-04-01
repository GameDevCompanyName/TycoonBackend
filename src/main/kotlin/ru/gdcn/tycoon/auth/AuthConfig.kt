package ru.gdcn.tycoon.auth

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.*
import io.ktor.http.HttpStatusCode
import io.ktor.http.Parameters
import io.ktor.request.receiveParameters
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.sessions.*

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import ru.gdcn.tycoon.api.conf.Response
import ru.gdcn.tycoon.api.conf.ResponseStatus
import ru.gdcn.tycoon.storage.StorageHelper
import ru.gdcn.tycoon.storage.entity.Player
import ru.gdcn.tycoon.storage.entity.Role
import ru.gdcn.tycoon.storage.entity.User
import ru.gdcn.tycoon.util.PassHashing

import java.io.File
import java.sql.Timestamp


class SessionToken(val username: String)

private const val PARAM_NAME_LOGIN_USERNAME = "username"
private const val PARAM_NAME_LOGIN_PASSWORD = "password"
private const val PARAM_NAME_REGISTRATION_PASSWORD_CONFIRM = "passwordConfirm"
private const val TOKEN_NAME = "TOKEN"

const val AUTH_FORM_NAME = "FormAuth"

private val logger: Logger by lazy { LoggerFactory.getLogger("AuthLogger") }

fun Application.installSession() {
    install(Sessions) {
        cookie<SessionToken>(TOKEN_NAME, directorySessionStorage(File("/sessionstor.txt"), false))
    }
}

fun Application.installAuth() {
    install(Authentication) {
        form(AUTH_FORM_NAME) {
            userParamName = PARAM_NAME_LOGIN_USERNAME
            passwordParamName = PARAM_NAME_LOGIN_PASSWORD
            challenge {
                call.respond(
                    Response(
                        ResponseStatus.ERROR.code,
                        "Invalid username or password!"
                    )
                )
            }
            validate { credentials ->
                val user = StorageHelper.userRepository.findByName(credentials.name)
                if (!user.isEmpty
                    && PassHashing.equalsPassword(
                        user.get().password,
                        credentials.password, user.get().salt
                    )
                ) {
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
    initRegistrationRoute(this)
    initAuthenticateRoute(this)
}

private fun initAuthenticateRoute(routing: Routing) {
    routing.authenticate(AUTH_FORM_NAME) {
        post("/login") {
            val principal = call.principal<UserIdPrincipal>()
            if (principal == null) {
                call.respond(
                    Response(
                        ResponseStatus.ERROR.code,
                        "Failed login!"
                    )
                )
                return@post
            }

            call.sessions.set(TOKEN_NAME, SessionToken(principal.name))
            call.respond(Response(ResponseStatus.OK.code, "User logged"))

            logger.info("\'${principal.name}\' logged")
        }
        get("/test") {
            call.respond(call.sessions.get<SessionToken>()?.username ?: "LOX")
        }
    }
}

private fun initRegistrationRoute(routing: Routing) {
    routing.post("/registration") {
        val parameters: Parameters
        try {
            parameters = call.receiveParameters()
        } catch (e: Exception) {
            e.printStackTrace()
            call.respond(
                Response(
                    ResponseStatus.ERROR.code,
                    "Can't receive parameters!"
                )
            )
            return@post
        }

        val username = parameters[PARAM_NAME_LOGIN_USERNAME]
        val password = parameters[PARAM_NAME_LOGIN_PASSWORD]
        val passwordConfirm = parameters[PARAM_NAME_REGISTRATION_PASSWORD_CONFIRM]
        if (username == null || password == null || passwordConfirm == null) {
            call.respond(
                Response(
                    ResponseStatus.ERROR.code,
                    "Incorrect key for parameters username or password or passwordConfirm!"
                )
            )
            return@post
        }

        val user = StorageHelper.userRepository.findByName(username)
        if (!user.isEmpty) {
            call.respond(
                Response(
                    ResponseStatus.ERROR.code,
                    "User already exists!"
                )
            )
            return@post
        }

        if (password != passwordConfirm) {
            call.respond(
                Response(
                    ResponseStatus.ERROR.code,
                    "Passwords not equals!"
                )
            )
            return@post
        }

        val pair = PassHashing.hashPassword(password)
        if (pair.isEmpty) {
            call.respond(
                Response(
                    ResponseStatus.ERROR.code,
                    "Failed to create a user!"
                )
            )
            return@post
        }

        val newUser = User(
            username = username,
            password = pair.get().first,
            salt = pair.get().second,
            role = Role.USER.id,
            registeredTimestamp = Timestamp(System.currentTimeMillis())
        )
        newUser.id = StorageHelper.userRepository.save(newUser)
        if (newUser.id == -1L) {
            call.respond(
                Response(
                    ResponseStatus.ERROR.code,
                    "Failed to create a user!"
                )
            )
            logger.error("\'${newUser.username}\' - failed to create. Cause: ¯\\_(ツ)_/¯")
            return@post
        }

        if (!createPlayerByUser(newUser)) {
            StorageHelper.userRepository.delete(newUser)
            call.respond(
                Response(
                    ResponseStatus.FAILED_CREATE_PLAYER.code,
                    "Failed to create a character!"
                )
            )
            logger.error("Failed to create a character. '${newUser.username}' was delete")
            return@post
        }

        call.sessions.set(SessionToken(newUser.username))
        call.respond(Response(ResponseStatus.OK.code, "User registered"))

        logger.info("\'${newUser.username}\' was registered!")
    }
}

private fun createPlayerByUser(user: User): Boolean {
    val cites = StorageHelper.cityRepository.findAll()
    if (cites.isEmpty()) {
        return false
    }

    val player = Player(
        name = user.username,
        money = 100,
        cityId = cites.random().id,
        userId = user.id
    )

    return StorageHelper.playerRepository.save(player) != -1L
}
