package controllers.action.auth

import controllers.ActionContext
import controllers.action.AbstractAction
import play.api.mvc.AnyContent
import play.api.mvc.PlainResult
import play.api.mvc.Request
import resources.MessageCode

object LogoutAction extends AbstractAction[Unit, Unit] {
  override def parseRequest(request: Request[AnyContent])(implicit context: ActionContext): Unit = {
  }

  override def executeAction(params: Unit)(implicit context: ActionContext): Unit = {
  }

  override def renderResult(values: Unit)(implicit context: ActionContext): PlainResult = {
    renderRedirect("/", MessageCode.MESSAGE_AUTH_LOGOUT).withNewSession
  }
}

