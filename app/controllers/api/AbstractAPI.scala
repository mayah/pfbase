package controllers.api

import java.nio.charset.Charset
import controllers.AbstractController
import play.api.Logger
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.mvc.PlainResult
import resources.ServerErrorCode
import resources.UserErrorCode
import controllers.ActionContext
import controllers.UserErrorControllerException

abstract class AbstractAPI[S, T] extends AbstractController[S, T] {
  private val UTF8 = Charset.forName("UTF8")
  private val RESULT_KEY = "result"
  private val DESCRIPTION_KEY = "description"
  private val OPTIONAL_INFO = "optionalInfo"

  // ----------------------------------------------------------------------

  def renderJson(obj: JsValue, status: Int = OK): PlainResult = {
    Status(status)(obj)
  }

  override protected def renderInvalid(ec: UserErrorCode.Code, e: Option[Throwable], optionalInfo: Option[Map[String, String]]): PlainResult = {
    e match {
      case None => ()
      case Some(x) => Logger.info("renderInvalid", x)
    }

    val json = Json.obj(
        RESULT_KEY -> "invalid",
        DESCRIPTION_KEY -> ec.description,
        OPTIONAL_INFO -> Json.toJson(optionalInfo.getOrElse(Map()))
    )

    renderJson(json, BAD_REQUEST)
  }

  override protected def renderError(ec: ServerErrorCode.Code, e: Option[Throwable], optionalInfo: Option[Map[String, String]]): PlainResult = {
    e match {
      case None => ()
      case Some(x) => Logger.info("renderInvalid", x)
    }

    val json = Json.obj(
        RESULT_KEY -> "error",
        DESCRIPTION_KEY -> ec.description,
        OPTIONAL_INFO -> Json.toJson(optionalInfo.getOrElse(Map()))
    )

    renderJson(json, INTERNAL_SERVER_ERROR)
  }

  override protected def renderLoginRequired(): PlainResult = {
    val json = Json.obj(
        RESULT_KEY -> "auth",
        DESCRIPTION_KEY -> "Login is required"
    )

    renderJson(json, UNAUTHORIZED).withHeaders(
        "WWW-Authenticate" -> "OAuth"
    )
  }

  override protected def renderForbidden(): PlainResult = {
    val json = Json.obj(
        RESULT_KEY -> "forbidden",
        DESCRIPTION_KEY -> "Forbidden action"
    )
    renderJson(json, FORBIDDEN)
  }

  override protected def renderNotFound(): PlainResult = {
    val json = Json.obj(
        RESULT_KEY -> "notfound",
        DESCRIPTION_KEY -> "Not found"
    )
    renderJson(json, NOT_FOUND)
  }
}
