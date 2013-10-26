package controllers.api.auth

import controllers.api.AbstractAPI
import resources.Constants
import resources.MessageCodes

object LogoutAPI extends AbstractAPI {
  def action = MPFFAction { request => implicit context =>
    val json = context.request.body.asJson

    println(json)

    ensureValidSessionToken(json)

    context.discardSession();
    context.discardLongLiveSession();

    context.addFlashing(Constants.Flash.MESSAGE_ID, MessageCodes.MESSAGE_AUTH_LOGOUT.descriptionId)
    renderOK();
  }
}
