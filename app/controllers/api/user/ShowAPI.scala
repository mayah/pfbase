package controllers.api.user

import java.sql.Connection

import controllers.api.AbstractAPI
import models.dto.User
import models.dto.UserId
import mpff.controllers.UserErrorControllerException
import play.api.Play.current
import play.api.db.DB
import resources.UserErrorCodes

object ShowAPI extends AbstractAPI {

  def action(id: String) = MPFFAction { request => implicit context =>
    val userId = parseUUID(id) match {
      case None => throw UserErrorControllerException(UserErrorCodes.INVALID_NOT_FOUND)
      case Some(userId) => UserId(userId)
    }

    val optUser = DB.withConnection { implicit con: Connection =>
      User.find(userId)
    }

    optUser match {
      case None => renderNotFound()
      case Some(user) => renderJson(user.toJSON())
    }
  }
}
