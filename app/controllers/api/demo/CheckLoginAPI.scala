package controllers.api.demo

import controllers.api.AbstractAPI

object CheckLoginAPI extends AbstractAPI {
  def action = MPFFAction { request => implicit context =>
    context.optLoginUser match {
      case None => renderLoginRequired()
      case Some(user) => renderOK()
    }
  }
}