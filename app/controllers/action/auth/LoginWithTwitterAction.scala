package controllers.action.auth
import controllers.action.AbstractAction
import play.api.mvc.Request
import play.api.mvc.Result
import controllers.ActionContext
import play.api.mvc.AnyContent
import play.api.mvc.Controller
import play.api.mvc.PlainResult
import app.AppGlobal
import resources.Constants
import play.api.cache.Cache
import play.api.Play.current
import twitter4j.TwitterException
import resources.ServerErrorCode

object LoginWithTwitterAction extends AbstractAction {
  private val LOGIN_TIMEOUT_SEC = 300

  def get = execute

  def doExecute(implicit context: ActionContext): PlainResult = {
    try {
      val request = context.request
      val twitterService = AppGlobal.twitterService
      val redirectURL: Option[String] = param("reidrectURL")
      val loginInfo = twitterService.createLoginInformation(redirectURL)

      val sessionId = request.session.get(Constants.Session.ID_KEY).getOrElse(throw new RuntimeException())
      Cache.set(Constants.Cache.TWITTER_LOGIN_KEY_PREFIX + sessionId, loginInfo, LOGIN_TIMEOUT_SEC);

      return renderRedirect(loginInfo.authenticationURL)
    } catch {
      case e: TwitterException =>
        renderError(ServerErrorCode.TWITTER_OAUTH_ERROR, e)
    }
  }
}

