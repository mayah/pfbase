package controllers.action.auth
import controllers.action.AbstractAction
import play.api.mvc.Action
import play.api.mvc.AnyContent
import controllers.ActionContext
import play.api.mvc.Request
import resources.MessageCode
import play.api.mvc.Result
import play.api.mvc.PlainResult

object LogoutAction extends AbstractAction {
  def get = execute

  def doExecute(implicit context: ActionContext): PlainResult =
    renderRedirect("/", MessageCode.MESSAGE_AUTH_LOGOUT).withNewSession

}

