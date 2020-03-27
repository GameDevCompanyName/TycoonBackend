package ru.gdcn.tycoon

import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import java.io.FileReader

object JSONHelper {

    private const val ENV_FILE_NAME = "environment"

    const val KEY_DB_USERNAME = "db.username"
    const val KEY_DB_PASSWORD = "db.password"
    const val KEY_SERVER_HOST = "server.host"
    const val KEY_SERVER_PORT = "server.port"

    val env by lazy { readEnv() }

    private fun readEnv(): Map<String, String> {
        val parser = JSONParser()
        val result = mutableMapOf<String, String>()
        try {
            val obj = parser.parse(FileReader(javaClass.classLoader.getResource(ENV_FILE_NAME).file)) as JSONObject
            result[KEY_DB_USERNAME] = obj[KEY_DB_USERNAME] as String
            result[KEY_DB_PASSWORD] = obj[KEY_DB_PASSWORD] as String
            result[KEY_SERVER_HOST] = obj[KEY_SERVER_HOST] as String
            result[KEY_SERVER_PORT] = obj[KEY_SERVER_PORT] as String
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }
}