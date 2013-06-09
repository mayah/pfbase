package controllers.api.user

import models.dto.User
import controllers.api.AbstractAPI
import play.api.mvc.Request
import play.api.mvc.AnyContent
import controllers.ActionContext
import models.ids.Id
import play.api.db.DB
import java.sql.Connection
import play.api.Play.current
import play.api.mvc.PlainResult

case class UserShowParams(val userIdStr: String)
case class UserShowValues(val optionalUser: Option[User])

object UserShowAPI extends AbstractAPI[UserShowParams, UserShowValues] {
  override def parseRequest(request: Request[AnyContent])(implicit context: ActionContext): UserShowParams = {
    val userIdStr: String = ensureQueryParam("userId").trim()

    return UserShowParams(userIdStr)
  }

  override def executeAction(params: UserShowParams)(implicit context: ActionContext): UserShowValues = {
    if (!Id.isValidId(params.userIdStr))
      return UserShowValues(None)

    DB.withConnection { implicit con: Connection =>
      return UserShowValues(User.find(params.userIdStr))
    }
  }

  override def renderResult(values: UserShowValues)(implicit context: ActionContext): PlainResult = {
    values.optionalUser match {
      case None => renderNotFound()
      case Some(user) => renderJson(user.toJSON())
    }
  }
}