package controllers.base

import java.util.UUID
import models.base.DAOException
import models.dto.User
import play.api.Logger
import play.api.Play.current
import play.api.db.DB
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.Controller
import play.api.mvc.PlainResult
import play.api.mvc.Request
import resources.Constants
import resources.MessageCode
import resources.ServerErrorCode
import resources.UserErrorCode
import java.sql.Connection

/**
 * In this framework, we make
 */
abstract class AbstractController[S, T] extends Controller {

  def parseRequest(request: Request[AnyContent])(implicit context: ActionContext): S
  def executeAction(params: S)(implicit context: ActionContext): T
  def renderResult(values: T)(implicit context: ActionContext): PlainResult

  // run is the main method of this Controller.
  // We have to define, parseRequest, executeAction, and renderResult
  def run = Action { request: Request[AnyContent] =>
    val beginTime = System.currentTimeMillis()

    implicit val context = prepareActionContext(request)
    val result: PlainResult = try {
      val params = parseRequest(request)
      val t = executeAction(params)
      renderResult(t)
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

    finalizeResult(result)
  }

  def prepareActionContext(request: Request[AnyContent]): ActionContext = {
    val maybeUserId: Option[String] = request.session.get(Constants.Session.USER_ID_KEY)
    val user: Option[User] = maybeUserId match {
      case None => None
      case Some(userId) => DB.withConnection { implicit con: Connection =>
        User.find(userId)
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

  def finalizeResult(result: PlainResult)(implicit context: ActionContext): PlainResult = {
    val r0 = result;
    val r1 = if (context.sessionsToAddResult.isEmpty) r0 else r0.withSession(context.sessionsToAddResult: _*)
    val r2 = if (context.headers.isEmpty) r1 else r1.withHeaders(context.headers: _*)
    return r2
  }

  // ----------------------------------------------------------------------
  // Rendering

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

    (e.maybeServerErrorCode, e.maybeUserErrorCode) match {
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
