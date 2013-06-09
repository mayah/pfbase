package controllers.api.auth

import java.sql.Connection
import org.apache.commons.codec.digest.DigestUtils
import controllers.ActionContext
import controllers.UserErrorControllerException
import controllers.api.AbstractAPI
import models.dto.User
import play.api.Play.current
import play.api.db.DB
import play.api.mvc.AnyContent
import play.api.mvc.PlainResult
import play.api.mvc.Request
import resources.UserErrorCode
import resources.Constants
import resources.MessageCode

case class LoginParams(val email: String, val password: String, val rememberMe: Boolean)
case class LoginValues(val user: User)

object LoginAPI extends AbstractAPI[LoginParams, LoginValues] {
  override def parseRequest(request: Request[AnyContent])(implicit context: ActionContext): LoginParams = {
    val email: String = ensureFormParam("email").trim()
    val password: String = ensureFormParam("password").trim()
    val rememberMe: Boolean = parseCheckBoxParam(ensureFormParam("rememberme").trim())
    return LoginParams(email, password, rememberMe)
  }

  override def executeAction(params: LoginParams)(implicit context: ActionContext): LoginValues = {
    val hashedPassword = DigestUtils.shaHex(params.password)
    val maybeUser = DB.withConnection { implicit con: Connection =>
      User.findBy(params.email, hashedPassword)
    }

    val user = maybeUser match {
      case None =>
        throw new UserErrorControllerException(UserErrorCode.INVALID_AUTH_PASSWORD, Map("password" -> "メールアドレスとパスワードが一致しません"))
      case Some(user) => user
    }

    if (params.rememberMe) {
      context.addLongLiveSessionValue(Constants.Session.USER_ID_KEY, user.id.toString())
    } else {
      context.addSessionValue(Constants.Session.USER_ID_KEY, user.id.toString())
    }
    return LoginValues(user)
  }

  override def renderResult(values: LoginValues)(implicit context: ActionContext): PlainResult = {
    context.addFlashing(Constants.Flash.MESSAGE_ID, MessageCode.MESSAGE_AUTH_LOGIN.descriptionId)
    return renderJson(values.user.toJSON())
  }
}