package ru.gdcn.tycoon.api.requests

import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.slf4j.LoggerFactory
import ru.gdcn.tycoon.api.FramesHandler
import ru.gdcn.tycoon.api.JSONWriterByResponseTypes
import ru.gdcn.tycoon.api.conf.*
import ru.gdcn.tycoon.storage.StorageHelper
import ru.gdcn.tycoon.storage.TransactionResult
import ru.gdcn.tycoon.storage.entity.City
import ru.gdcn.tycoon.storage.entity.CityResource
import ru.gdcn.tycoon.storage.entity.PlayerResource

object DealRequest : BaseRequest() {

    private val logger = LoggerFactory.getLogger(DealRequest::class.java)

    override suspend fun execute(
        username: String,
        request: Request,
        actionListener: FramesHandler.RequestExecutorListener
    ) {
        val jsonItems = request.parameters["items"]
        if (jsonItems == null || jsonItems !is JSONArray) {
            actionListener.onSendResponse(
                username,
                Response.createErrorResponseJSONString(
                    ResponseType.deal,
                    ResponseCauseText.FAILED_PARAM.text
                )
            )
            return
        }

        //TODO сделать красиво с проверкой
        val itemsForDeal = (jsonItems).map {
            val obj = it as JSONObject
            DealItem(obj["id"] as Long, obj["action"] as String, obj["quantity"] as Long)
        }

        val cityInfo = getCity(username)
        if (cityInfo.isEmpty) {
            actionListener.onSendResponse(
                username,
                Response.createErrorResponseJSONString(
                    ResponseType.deal,
                    ResponseCauseText.FAILED_CITY_NOT_EXIST.text
                )
            )
            return
        }

        val playerInfo = getPlayer(username)
        if (playerInfo.isEmpty) {
            actionListener.onSendResponse(
                username,
                Response.createErrorResponseJSONString(
                    ResponseType.deal,
                    ResponseCauseText.FAILED_PLAYER_NOT_EXIST.text
                )
            )
            return
        }

        val playerResource = StorageHelper.transaction {
            val pr = StorageHelper.playerResourceRepository.findByPlayerId(it, playerInfo.get().id)
                ?: return@transaction TransactionResult<MutableList<PlayerResource>>(true, null)

            return@transaction TransactionResult(true, pr.toMutableList())
        }
        if (playerResource.isEmpty) {
            actionListener.onSendResponse(
                username,
                Response.createErrorResponseJSONString(
                    ResponseType.deal,
                    ResponseCauseText.FAILED_DEAL_PLAYER_HAVE_NO_RESOURCE.text
                )
            )
        }

        val cityResource = StorageHelper.transaction {
            val cr = StorageHelper.cityResourceRepository.findByCityId(it, cityInfo.get().id)
                ?: return@transaction TransactionResult<List<CityResource>>(true, null)

            return@transaction TransactionResult(true, cr)
        }
        if (cityResource.isEmpty) {
            actionListener.onSendResponse(
                username,
                Response.createErrorResponseJSONString(
                    ResponseType.deal,
                    ResponseCauseText.FAILED_DEAL_CITY_HAVE_NO_RESOURCE.text
                )
            )
        }

        var sumDeal = 0L
        itemsForDeal.forEach { item ->
            val cr = cityResource.get().filter { it.compositeId.resourceId == item.id }
            if (cr.isEmpty()) {
                actionListener.onSendResponse(
                    username,
                    Response.createErrorResponseJSONString(
                        ResponseType.deal,
                        ResponseCauseText.FAILED_DEAL_CITY_HAVE_NO_RESOURCE.text
                    )
                )
                return
            }

            val pr = playerResource.get().filter { it.compositeId.resourceId == item.id }

            if (cr.size != 1) {
                logger.error("БД хуйня, элементов для сделки слишком много!")
            }

            if (item.action == "buy") {
                if (cr.first().quantity < item.quantity) {
                    actionListener.onSendResponse(
                        username,
                        Response.createErrorResponseJSONString(
                            ResponseType.deal,
                            ResponseCauseText.FAILED_DEAL_CITY_HAVE_NO_RESOURCE.text
                        )
                    )
                    return
                } else {
                    sumDeal += item.quantity * cr.first().cost
                    cr.first().quantity -= item.quantity

                    if (pr.isEmpty()) {
                        val newPR = PlayerResource(item.id, playerInfo.get().id, item.quantity)
                        playerResource.get().add(newPR)
                    } else {
                        pr.first().quantity += item.quantity
                    }
                }
            } else if (item.action == "sell") {
                if (pr.isEmpty()) {
                    actionListener.onSendResponse(
                        username,
                        Response.createErrorResponseJSONString(
                            ResponseType.deal,
                            ResponseCauseText.FAILED_DEAL_PLAYER_HAVE_NO_RESOURCE.text
                        )
                    )
                    return
                }

                if (pr.size != 1) {
                    logger.error("БД хуйня, элементов для сделки слишком много!")
                }

                if (pr.first().quantity < item.quantity) {
                    actionListener.onSendResponse(
                        username,
                        Response.createErrorResponseJSONString(
                            ResponseType.deal,
                            ResponseCauseText.FAILED_DEAL_PLAYER_HAVE_NO_RESOURCE.text
                        )
                    )
                    return
                } else {
                    sumDeal -= item.quantity * cr.first().cost
                    pr.first().quantity -= item.quantity
                    cr.first().quantity += item.quantity
                }
            } else {
                actionListener.onSendResponse(
                    username,
                    Response.createErrorResponseJSONString(
                        ResponseType.deal,
                        ResponseCauseText.FAILED_PARAM.text
                    )
                )
                return
            }
        }

        if (sumDeal > playerInfo.get().money) {
            actionListener.onSendResponse(
                username,
                Response.createErrorResponseJSONString(
                    ResponseType.deal,
                    ResponseCauseText.FAILED_DEAL_PLAYER_HAVE_NO_MONEY.text
                )
            )
            return
        } else {
            playerInfo.get().money -= sumDeal
        }

        val resultTransaction = StorageHelper.transaction { session ->
            cityResource.get().forEach {
                StorageHelper.cityResourceRepository.update(session, it)
            }
            playerResource.get().forEach {
                StorageHelper.playerResourceRepository.saveOrUpdate(session, it)
            }

            StorageHelper.playerRepository.update(session, playerInfo.get())

            return@transaction TransactionResult(
                isRollback = false,
                data = true
            )
        }

        if (resultTransaction.isEmpty) {
            actionListener.onSendResponse(
                username,
                Response.createErrorResponseJSONString(
                    ResponseType.deal,
                    ResponseCauseText.FAILED_DEAL.text
                )
            )
            return
        } else {
            val city = getCityById(playerInfo.get().cityId)
            val player = getPlayer(username)

            actionListener.onSendResponse(
                username,
                Response(
                    ResponseStatus.OK.code,
                    ResponseType.deal.name,
                    JSONWriterByResponseTypes.createObjectForDeal(city.get(), player.get())
                ).toJSONString()
            )
        }

        val freshCity = getCityById(playerInfo.get().cityId)
        if (freshCity.isPresent) {
            val obj = JSONObject()
            obj["city"] = JSONWriterByResponseTypes.createObjectForCity(freshCity.get())
            actionListener.onSendEventTo(
                freshCity.get().players.map { it.name },
                Response(
                    ResponseStatus.OK.code,
                    ResponseType.city.name,
                    obj
                ).toJSONString()
            )
        }
    }

    private data class DealItem(val id: Long, val action: String, val quantity: Long)
}