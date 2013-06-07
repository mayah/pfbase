package controllers.action.auth

import app.AppGlobal
import controllers.ActionContext
import controllers.ServerErrorControllerException
import controllers.action.AbstractAction
import play.api.Play.current
import play.api.cache.Cache
import play.api.mvc.AnyContent
import play.api.mvc.PlainResult
import play.api.mvc.Request
import resources.Constants
import resources.ServerErrorCode
import sessions.TwitterLoginInformation
import twitter4j.TwitterException

case class LoginWithTwitterParams(
    val redirectURL: Option[String]
)
case class LoginWithTwitterValues(
    val loginInfo: TwitterLoginInformation
)

object LoginWithTwitterAction extends AbstractAction[LoginWithTwitterParams, LoginWithTwitterValues] {
  class Params
  private val LOGIN_TIMEOUT_SEC = 300

  override def parseRequest(request: Request[AnyContent])(implicit context: ActionContext): LoginWithTwitterParams = {
    val redirectURL = queryParam("redirectURL")
    return LoginWithTwitterParams(redirectURL)
  }

  override def executeAction(params: LoginWithTwitterParams)(implicit context: ActionContext): LoginWithTwitterValues = {
    try {
      val loginInfo = AppGlobal.twitterService.createLoginInformation(params.redirectURL)
      val sessionId = context.request.session.get(Constants.Session.ID_KEY).getOrElse(throw new RuntimeException())
      Cache.set(Constants.Cache.TWITTER_LOGIN_KEY_PREFIX + sessionId, loginInfo, LOGIN_TIMEOUT_SEC);

      return LoginWithTwitterValues(loginInfo)
    } catch {
      case e: TwitterException => throw ServerErrorControllerException(ServerErrorCode.TWITTER_OAUTH_ERROR, Option(e))
    }
  }

  override def renderResult(values: LoginWithTwitterValues)(implicit context: ActionContext): PlainResult = {
    return renderRedirect(values.loginInfo.authenticationURL)
  }
}

