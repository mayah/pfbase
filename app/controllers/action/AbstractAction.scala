package controllers.action
import controllers.AbstractController
import play.api.mvc.Content
import play.api.mvc.PlainResult
import play.api.Logger
import resources.ServerErrorCode
import resources.UserErrorCode
import play.api.templates.Html
import controllers.ActionContext

abstract class AbstractAction extends AbstractController {

  protected def render(content: Html): PlainResult = Ok(content)

  override protected def renderInvalid(ec: UserErrorCode.Code, e: Option[Throwable] = None): PlainResult = {
    e match {
      case None => BadRequest
      case Some(x) =>
        Logger.info("renderInvalid", x)
        Redirect("/invalid?errorCode=" + ec.errorDescriptionId)
    }
  }

  override protected def renderError(ec: ServerErrorCode.Code, e: Option[Throwable] = None): PlainResult = {
    e match {
      case None => InternalServerError
      case Some(x) =>
        Logger.info("renderError", x);
        Redirect("/error?errorCode=" + ec.errorDescriptionId)
    }
  }

  override protected def renderLoginRequired(): PlainResult = Redirect("/loginRequired")

  override protected def renderForbidden(): PlainResult = Forbidden

  override protected def renderNotFound(): PlainResult = NotFound

}