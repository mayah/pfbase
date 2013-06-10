package controllers.action

import controllers.ActionContext
import play.api.mvc.SimpleResult
import play.api.mvc.Request
import play.api.mvc.AnyContent

object HTMLAction extends AbstractAction {
  def action(param: String) = MPFFAction { request => implicit context =>
    renderHTML(views.html.index())
  }
}
