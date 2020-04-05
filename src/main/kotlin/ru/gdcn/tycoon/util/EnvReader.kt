package ru.gdcn.tycoon.util

import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser

import java.io.FileReader

object EnvReader {

    private const val ENV_FILE_NAME = "/environment.txt"

    const val KEY_DB_USERNAME = "db.username"
    const val KEY_DB_PASSWORD = "db.password"
    const val KEY_SERVER_HOST = "server.host"
    const val KEY_SERVER_PORT = "server.port"
    const val KEY_SERVER_HASHING_PASS_ITERATION_COUNT = "server.hashing_pass.iteration_count"
    const val KEY_SERVER_HASHING_PASS_KEY_LENGTH = "server.hashing_pass.key_length"

    val env by lazy { readEnv() }

    private fun readEnv(): Map<String, String> {
        val parser = JSONParser()
        val result = mutableMapOf<String, String>()
        try {
            val obj = parser.parse(javaClass.getResourceAsStream(ENV_FILE_NAME).bufferedReader()) as JSONObject
            result[KEY_DB_USERNAME] = obj[KEY_DB_USERNAME] as String
            result[KEY_DB_PASSWORD] = obj[KEY_DB_PASSWORD] as String
            result[KEY_SERVER_HOST] = obj[KEY_SERVER_HOST] as String
            result[KEY_SERVER_PORT] = obj[KEY_SERVER_PORT] as String
            result[KEY_SERVER_HASHING_PASS_ITERATION_COUNT] = obj[KEY_SERVER_HASHING_PASS_ITERATION_COUNT] as String
            result[KEY_SERVER_HASHING_PASS_KEY_LENGTH] = obj[KEY_SERVER_HASHING_PASS_KEY_LENGTH] as String
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }
}
