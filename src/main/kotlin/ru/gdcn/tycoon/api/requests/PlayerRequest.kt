package ru.gdcn.tycoon.api.requests

import org.json.simple.JSONObject
import ru.gdcn.tycoon.api.FramesHandler
import ru.gdcn.tycoon.api.conf.*
import ru.gdcn.tycoon.storage.entity.Player

object PlayerRequest : BaseRequest() {
    override suspend fun execute(
        username: String,
        request: Request,
        actionListener: FramesHandler.RequestExecutorListener
    ) {
        val player = getPlayer(username)
        if (player.isEmpty) {
            actionListener.onSendResponse(
                username,
                Response(
                    ResponseStatus.ERROR.code,
                    ResponseType.player.name,
                    ResponseCauseText.FAILED_GET_INFO.text
                ).toJSONString()
            )
        } else {
            val jsonObject = player.get().toJSONObject(Player.FIELD_ALL)
            val tmpObj = JSONObject()
            tmpObj["player"] = jsonObject
            actionListener.onSendResponse(
                username,
                Response(
                    ResponseStatus.OK.code,
                    ResponseType.player.name,
                    tmpObj
                ).toJSONString()
            )
        }
    }
}