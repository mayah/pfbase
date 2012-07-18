package controllers
import java.util.UUID
import models.dao.DAOException
import play.api.Play.current
import play.api.db.DB
import play.api.mvc.Request
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.Controller
import play.api.mvc.PlainResult
import play.api.Logger
import resources.Constants
import resources.MessageCode
import resources.ServerErrorCode
import resources.UserErrorCode
import models.dto.User

abstract class AbstractController extends Controller {
  def doExecute(implicit context: ActionContext): PlainResult

  def execute = Action { request: Request[AnyContent] =>
    val beginTime = System.currentTimeMillis()
    val context = ensureContext(request)
    val result = try {
      doExecute(context)
    } catch {
      case e: DAOException =>
        renderError(ServerErrorCode.ERROR_DATABASE, e)
      case e: ControllerException =>
        renderException(e)
      case e: Exception =>
        renderError(ServerErrorCode.ERROR_UNKNOWN, e)
    } finally {
      val endTime = System.currentTimeMillis()
      Logger.info(request.uri + " took " + (endTime - beginTime) + "[msec] to process.")
    }

    result.withSession(
      context.sessionsToAddResult.foldRight(context.request.session) { (kv, s) => s + kv }
    )
  }

  private def ensureContext(request: Request[AnyContent]): ActionContext = {
    val userId: Option[String] = request.session.get(Constants.Session.USER_ID_KEY)

    val user: Option[User] = userId match {
      case None => None
      case Some(id) => DB.withConnection { implicit con =>
          User.find(id)
      }
    }

    val currentURL: String = request.uri

    var sessionsToBeAdded: List[(String, String)] = List.empty

    val sessionToken: String = request.session.get(Constants.Session.TOKEN_KEY) match {
      case None =>
        var token = UUID.randomUUID().toString()
        sessionsToBeAdded = (Constants.Session.TOKEN_KEY -> token) :: sessionsToBeAdded
        token
      case Some(x) => x
    }

    val sessionId: String = request.session.get(Constants.Session.ID_KEY) match {
      case None =>
        val id = UUID.randomUUID().toString()
        sessionsToBeAdded = (Constants.Session.ID_KEY -> UUID.randomUUID().toString()) :: sessionsToBeAdded
        id
      case Some(x) => x
    }

    val context = new ActionContext(user, request, sessionToken, sessionId, currentURL)

    for (kv <- sessionsToBeAdded)
      context.shouldAddToSession(kv._1, kv._2)

    return context
  }

  protected def renderInvalid(ec: UserErrorCode.Code, e: Option[Throwable] = None): PlainResult
  protected def renderError(ec: ServerErrorCode.Code, e: Option[Throwable] = None): PlainResult
  protected def renderLoginRequired(): PlainResult
  protected def renderForbidden(): PlainResult
  protected def renderNotFound(): PlainResult

  protected def renderInvalid(ec: UserErrorCode.Code, e: Throwable): PlainResult = renderInvalid(ec, Option(e))
  protected def renderError(ec: ServerErrorCode.Code, e: Throwable): PlainResult = renderError(ec, Option(e))

  protected def renderRedirect(url: String): PlainResult =
    renderRedirect(url, None)

  protected def renderRedirect(url: String, code: MessageCode.Code): PlainResult =
    renderRedirect(url, Option(code))

  protected def renderRedirect(url: String, code: Option[MessageCode.Code]): PlainResult = {
    code match {
      case None => Redirect(url)
      case Some(c) => Redirect(url).flashing(
        Constants.Flash.MESSAGE_ID -> c.descriptionId
      )
    }
  }

  protected def renderException(e: ControllerException): PlainResult = {
    e.statusCode match {
      case 401 =>
        return renderLoginRequired()
      case 403 =>
        return renderForbidden()
      case 404 =>
        return renderNotFound()
    }

    (e.serverErrorCode, e.userErrorCode) match {
      case (Some(ec), None) =>
        return renderError(ec, e.getCause())
      case (None, Some(ec)) =>
        return renderInvalid(ec, e.getCause())
      case _ =>
        assert(false)
        throw new RuntimeException()
    }
  }

  // ----------------------------------------------------------------------

  def param(key: String)(implicit context: ActionContext) = {
    context.request.queryString.get(key) match {
      case None => None
      case Some(values) => values.headOption
    }
  }
}

