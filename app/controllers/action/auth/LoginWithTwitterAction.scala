package controllers.action.auth
import controllers.action.AbstractAction
import play.api.mvc.Request
import play.api.mvc.Result
import controllers.base.ActionContext
import play.api.mvc.AnyContent
import play.api.mvc.Controller
import play.api.mvc.PlainResult
import app.AppGlobal
import resources.Constants
import play.api.cache.Cache
import play.api.Play.current
import twitter4j.TwitterException
import resources.ServerErrorCode
import sessions.TwitterLoginInformation
import controllers.base.ServerErrorControllerException

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
    val redirectURL = param("redirectURL")
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

