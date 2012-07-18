package controllers.action
import controllers.ActionContext
import play.api.mvc.Action
import play.api.mvc.Request
import play.api.mvc.AnyContent
import play.api.mvc.PlainResult

object IndexAction extends AbstractAction {

  def get = execute

  def doExecute(implicit context: ActionContext): PlainResult = {
    return render(views.html.index("Your new application is ready."))
  }

}
