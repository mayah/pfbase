package controllers.api.user

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
import play.api.mvc.Cookie
import play.api.mvc.Session
import resources.MessageCode

case class UserLoginParams(val email: String, val password: String, val rememberMe: Boolean)
case class UserLoginValues(val user: User)

object UserLoginAPI extends AbstractAPI[UserLoginParams, UserLoginValues] {
  override def parseRequest(request: Request[AnyContent])(implicit context: ActionContext): UserLoginParams = {
    val email: String = ensureFormParam("email").trim()
    val password: String = ensureFormParam("password").trim()
    val rememberMe: Boolean = parseCheckBoxParam(ensureFormParam("rememberme").trim())
    return UserLoginParams(email, password, rememberMe)
  }

  override def executeAction(params: UserLoginParams)(implicit context: ActionContext): UserLoginValues = {
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
    return UserLoginValues(user)
  }

  override def renderResult(values: UserLoginValues)(implicit context: ActionContext): PlainResult = {
    context.addFlashing(Constants.Flash.MESSAGE_ID, MessageCode.MESSAGE_AUTH_LOGIN.descriptionId)
    return renderJson(values.user.toJSON())
  }
}