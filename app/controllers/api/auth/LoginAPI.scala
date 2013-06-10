package controllers.api.auth

import java.sql.Connection

import org.apache.commons.codec.digest.DigestUtils

import controllers.api.AbstractAPI
import models.dto.User
import mpff.controllers.UserErrorControllerException
import play.api.Play.current
import play.api.db.DB
import resources.Constants
import resources.MessageCodes
import resources.UserErrorCodes

case class LoginParams(val email: String, val password: String, val rememberMe: Boolean)
case class LoginValues(val user: User)

object LoginAPI extends AbstractAPI {
  def action = MPFFAction { request => implicit context =>
    val optJson = request.body.asJson
    ensureValidSessionToken(optJson)

    val email = jsonEnsureString(optJson, "email").trim()
    val password = jsonEnsureString(optJson, "password").trim()
    val rememberMe = jsonOptBoolean(optJson, "rememberme").getOrElse(false)

    val hashedPassword = DigestUtils.shaHex(password)

    val user = (DB.withConnection { implicit con: Connection =>
      User.findByEmailAndPassword(email, hashedPassword)
    }) match {
      case None => throw new UserErrorControllerException(UserErrorCodes.INVALID_AUTH_PASSWORD, Map("password" -> "メールアドレスとパスワードが一致しません"))
      case Some(user) => user
    }

    if (rememberMe) {
      context.addLongLiveSessionValue(Constants.Session.USER_ID_KEY, user.id.toString())
    } else {
      context.addSessionValue(Constants.Session.USER_ID_KEY, user.id.toString())
    }

    context.addFlashing(Constants.Flash.MESSAGE_ID, MessageCodes.MESSAGE_AUTH_LOGIN.descriptionId)
    renderJson(user.toJSON())
  }
}
