package ru.gdcn.tycoon.api.requests

import org.json.simple.JSONArray
import org.json.simple.JSONObject
import ru.gdcn.tycoon.api.FramesHandler
import ru.gdcn.tycoon.api.JSONWriterByResponseTypes
import ru.gdcn.tycoon.api.conf.*
import ru.gdcn.tycoon.storage.entity.City

object CityRequest : BaseRequest() {
    override suspend fun execute(
        username: String,
        request: Request,
        actionListener: FramesHandler.RequestExecutorListener
    ) {
        val city = getCity(username)
        if (city.isEmpty) {
            actionListener.onSendResponse(
                username,
                Response(
                    ResponseStatus.ERROR.code,
                    ResponseType.city.name,
                    ResponseCauseText.FAILED_GET_INFO.text
                ).toJSONString()
            )
        } else {
            val tmpObj = JSONObject()
            tmpObj["city"] = JSONWriterByResponseTypes.createObjectForCity(city.get())
            actionListener.onSendResponse(
                username,
                Response(
                    ResponseStatus.OK.code,
                    request.method,
                    tmpObj
                ).toJSONString()
            )
        }
    }
}