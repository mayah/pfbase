package controllers.action.auth

import controllers.action.AbstractAction
import controllers.base.ActionContext
import play.api.mvc.AnyContent
import play.api.mvc.PlainResult
import play.api.mvc.Request
import play.api.mvc.Result
import resources.UserErrorCode
import resources.ServerErrorCode
import resources.MessageCode
import resources.Constants
import play.api.cache.Cache
import play.api.Play.current
import sessions.TwitterLoginInformation
import twitter4j.TwitterException
import app.AppGlobal
import models.dto.User
import models.dto.UserTwitterLinkEmbryo
import play.api.db.DB
import models.dto.UserTwitterLink
import java.sql.Connection
import models.dto.UserTwitterLink
import models.dto.UserEmbryo
import models.ids.UserId
import controllers.base.ServerErrorControllerException
import controllers.base.UserErrorControllerException
import controllers.base.UserErrorControllerException
import controllers.base.ServerErrorControllerException

case class VerifyForTwitterParams(
    val verifier: String
)
case class VerifyForTwitterValues(
    val loginInfo: TwitterLoginInformation,
    val messageCode: MessageCode.Code
)

object VerifyForTwitterAction extends AbstractAction[VerifyForTwitterParams, VerifyForTwitterValues] {
  override def parseRequest(request: Request[AnyContent])(implicit context: ActionContext): VerifyForTwitterParams = {
    val verifier: String = param("oauth_verifier") match {
      case None =>
        throw new UserErrorControllerException(UserErrorCode.INVALID_OAUTH_VERIFIER)
      case Some(x) => x
    }

    return VerifyForTwitterParams(verifier)
  }

  override def executeAction(params: VerifyForTwitterParams)(implicit context: ActionContext): VerifyForTwitterValues = {
    val sessionId: String = context.sessionId
    val loginInfo: TwitterLoginInformation = Cache.get(Constants.Cache.TWITTER_LOGIN_KEY_PREFIX + sessionId) match {
      case None => throw UserErrorControllerException(UserErrorCode.INVALID_UNEXPECTED_REQUEST)
      case Some(x) => x.asInstanceOf[TwitterLoginInformation]
    }
    Cache.remove(Constants.Cache.TWITTER_LOGIN_KEY_PREFIX + sessionId)

    val messageCode = try {
      val twitterService = AppGlobal.twitterService
      val embryo = twitterService.createTwitterLinkFromLoginInformation(loginInfo, params.verifier)
      val user: User = loadFromTwitterLinkEmbryo(embryo)
      context.shouldAddToSession(Constants.Session.USER_ID_KEY, user.userId.toString())

      MessageCode.MESSAGE_AUTH_LOGIN
    } catch {
      case e: TwitterException => throw ServerErrorControllerException(ServerErrorCode.TWITTER_OAUTH_ERROR, Option(e));
    }

    return VerifyForTwitterValues(loginInfo, messageCode)
  }

  override def renderResult(values: VerifyForTwitterValues)(implicit context: ActionContext): PlainResult = {
    val redirectURL = values.loginInfo.redirectURL match {
      case None =>
        return renderRedirect("/", values.messageCode);
      case Some(x) =>
        x
    }

    return renderRedirect(redirectURL, values.messageCode);
  }

  private def loadFromTwitterLinkEmbryo(embryo: UserTwitterLinkEmbryo): User = {
    try {
      DB.withConnection { implicit con =>
        updateTwitterLinkAndUser(embryo);
      }
    } catch {
      case e: TwitterException =>
        throw new ServerErrorControllerException(ServerErrorCode.TWITTER_OAUTH_ERROR, e);
    }
  }

  private def updateTwitterLinkAndUser(embryo: UserTwitterLinkEmbryo)(implicit con: Connection): User = {
    UserTwitterLink.findByTwitterId(embryo.twitterId) match {
      case None => // When there is no TwitterLink, we have to create a user.
        val userId = User.create(new UserEmbryo(embryo.screenName))
        val twitterLinkId = UserTwitterLink.create(embryo, userId)
        new User(userId, embryo.screenName)
      case Some(x) =>
        User.find(x.userId) match {
          case None =>
            val userEmbryo = new UserEmbryo(embryo.screenName)
            User.create(x.userId, userEmbryo)
          case Some(y) =>
            y
        }
    }
  }
}


