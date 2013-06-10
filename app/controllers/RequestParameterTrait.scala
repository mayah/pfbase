package controllers

import mpff.controllers.MPFFRequestParameterTrait
import models.dto.User
import mpff.controllers.UserErrorControllerException
import mpff.controllers.UserErrorControllerException
import resources.UserErrorCodes
import play.api.libs.json.JsValue
import resources.Constants

trait RequestParameterTrait extends MPFFRequestParameterTrait[ActionContext] {
  def ensureLoginUser()(implicit context: ActionContext): User = {
    context.optLoginUser match {
      case None => throw UserErrorControllerException(UserErrorCodes.INVALID_LOGIN_REQUIRED)
      case Some(loginUser) => loginUser
    }
  }
}
