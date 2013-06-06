package controllers.action

import controllers.base.ActionContext
import play.api.mvc.PlainResult
import play.api.mvc.Request
import play.api.mvc.AnyContent

object IndexAction extends AbstractAction[Unit, Unit] {
  def parseRequest(request: Request[AnyContent])(implicit context: ActionContext): Unit = {
  }

  def executeAction(params: Unit)(implicit context: ActionContext): Unit = {
  }

  def renderResult(values: Unit)(implicit context: ActionContext): PlainResult = {
    return render(views.html.index("Your new application is ready."))
  }
}
