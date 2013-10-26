package controllers.api.user

import java.sql.Connection

import org.apache.commons.codec.digest.DigestUtils

import controllers.api.AbstractAPI
import models.dto.User
import models.dto.UserEmbryo
import mpff.controllers.UserErrorControllerException
import play.api.Play.current
import play.api.db.DB
import resources.Constants
import resources.UserErrorCodes


object CreateAPI extends AbstractAPI {

  def action = MPFFAction { request => implicit context =>
    val optJson = request.body.asJson

    val loginId = jsonEnsureString(optJson, "loginId").trim()
    val email = jsonEnsureString(optJson, "email").trim()
    val nickname = jsonEnsureString(optJson, "nickname").trim()
    val password = jsonEnsureString(optJson, "password").trim()

    val hashedPassword = DigestUtils.shaHex(password)

    // Register UserEmailPassword and User.
    val user: User = DB.withTransaction { implicit con: Connection =>
      val userEmbryo = new UserEmbryo(loginId, nickname, email, hashedPassword)
      val userId = User.create(userEmbryo)
      User.find(userId).get
    }

    context.addSessionValue(Constants.Session.USER_ID_KEY, user.id.toString())

    renderJson(user.toJSON())
  }
}
