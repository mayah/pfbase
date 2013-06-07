package controllers

import java.sql.Connection
import java.util.UUID

import models.DAOException
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
      val values = executeAction(params)
      renderResult(values)
    } catch {
      case e: DAOException =>
        renderError(ServerErrorCode.ERROR_DATABASE, Some(e))
      case e: ControllerException =>
        renderException(e)
      case e: Exception =>
        renderError(ServerErrorCode.ERROR_UNKNOWN, Some(e))
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

    val sessionToken: String = request.session.get(Constants.Session.TOKEN_KEY) match {
      case None => UUID.randomUUID().toString()
      case Some(x) => x
    }

    val sessionId: String = request.session.get(Constants.Session.ID_KEY) match {
      case None => UUID.randomUUID().toString()
      case Some(x) => x
    }

    val context = new ActionContext(user, request, sessionToken, sessionId, currentURL)

    // --- prepare session values
    context.addSessionValue(Constants.Session.TOKEN_KEY, sessionToken)
    context.addSessionValue(Constants.Session.ID_KEY, sessionId)
    if (user != None)
        context.addSessionValue(Constants.Session.USER_ID_KEY, user.get.id.toString)

    return context
  }

  def finalizeResult(result: PlainResult)(implicit context: ActionContext): PlainResult = {
    val r1 = result.withSession(context.sessionValues.reverse: _*)
    val r2 = if (context.headers.isEmpty) r1 else r1.withHeaders(context.headers: _*)
    return r2
  }

  // ----------------------------------------------------------------------
  // Rendering

  protected def renderInvalid(ec: UserErrorCode.Code, e: Option[Throwable] = None, optionalInfo: Option[Map[String, String]] = None): PlainResult
  protected def renderError(ec: ServerErrorCode.Code, e: Option[Throwable] = None, optionalInfo: Option[Map[String, String]] = None): PlainResult
  protected def renderLoginRequired(): PlainResult
  protected def renderForbidden(): PlainResult
  protected def renderNotFound(): PlainResult

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
      case _ =>
        ()
    }

    if (e.isInstanceOf[ServerErrorControllerException]) {
      val se = e.asInstanceOf[ServerErrorControllerException]
      return renderError(se.errorCode, se.optionalCause, se.optionalInfo);
    }

    if (e.isInstanceOf[UserErrorControllerException]) {
      val ue = e.asInstanceOf[UserErrorControllerException]
      return renderInvalid(ue.errorCode, ue.optionalCause, ue.optionalInfo);
    }

    assert(false);
    throw new RuntimeException()
  }

  // ----------------------------------------------------------------------

  def queryParam(key: String)(implicit context: ActionContext): Option[String] = {
    context.request.queryString.get(key) match {
      case None => None
      case Some(values) => values.headOption
    }
  }

  def queryMultipleParams(key: String)(implicit context: ActionContext): Option[Seq[String]] = {
    context.request.queryString.get(key)
  }

  def formParam(key: String)(implicit context: ActionContext): Option[String] = {
    context.request.body.asFormUrlEncoded match {
      case None => return None
      case Some(map) =>
        map.get(key).headOption match {
          case None => return None
          case Some(seq) => return seq.headOption
        }
    }
  }

  def ensureQueryParam(key: String)(implicit context: ActionContext): String = {
    queryParam(key) match {
      case None =>
        throw new UserErrorControllerException(UserErrorCode.INVALID_FORM_PARAMETERS, Map(key -> "missing"))
      case Some(x) => x
    }
  }

  def ensureFormParam(key: String)(implicit context: ActionContext): String = {
    formParam(key) match {
      case None =>
        throw new UserErrorControllerException(UserErrorCode.INVALID_FORM_PARAMETERS, Map(key -> "missing"))
      case Some(x) => x
    }
  }


}

