package controllers.api
import java.nio.charset.Charset
import controllers.AbstractController
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.mvc.PlainResult
import play.api.Logger
import resources.ServerErrorCode
import resources.UserErrorCode
import controllers.ActionContext

abstract class AbstractAPI extends AbstractController {
  private val UTF8 = Charset.forName("UTF8")

  def renderJson(obj: JsValue, status: Int): PlainResult = {
    Status(status)(obj)
  }

  override protected def renderInvalid(ec: UserErrorCode.Code, e: Option[Throwable]): PlainResult = {
    e match {
      case None => ()
      case Some(x) => Logger.info("renderInvalid", x)
    }

    val obj = Json.toJson(Map(
        "result" -> "invalid",
        "reason" -> ec.reasonString
    ))

    renderJson(obj, BAD_REQUEST)
  }

  override protected def renderError(ec: ServerErrorCode.Code, e: Option[Throwable]): PlainResult = {
    e match {
      case None => ()
      case Some(x) => Logger.info("renderInvalid", x)
    }

    val obj = Json.toJson(Map(
        "result" -> "error",
        "reason" -> ec.reasonString
    ))

    renderJson(obj, INTERNAL_SERVER_ERROR)
  }


  override protected def renderLoginRequired(): PlainResult = {
    val json = Json.toJson(Map(
        "result" -> "auth",
        "reason" -> "login is required"
    ))
    renderJson(json, UNAUTHORIZED).withHeaders(
        "WWW-Authenticate" -> "OAuth"
    )
  }

  override protected def renderForbidden(): PlainResult = {
    val json = Json.toJson(Map(
        "result" -> "forbidden",
        "reason" -> "forbidden action"
    ))
    renderJson(json, FORBIDDEN)
  }

  override protected def renderNotFound(): PlainResult = {
    val json = Json.toJson(Map(
        "result" -> "notfound",
        "reason" -> "not found"
    ))
    renderJson(json, NOT_FOUND)
  }
}
