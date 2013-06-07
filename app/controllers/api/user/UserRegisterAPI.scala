package controllers.api.user

import controllers.api.AbstractAPI
import play.api.mvc.Request
import controllers.ActionContext
import play.api.mvc.PlainResult
import play.api.mvc.AnyContent
import models.dto.User
import models.dto.UserEmailPassword
import controllers.UserErrorControllerException
import resources.UserErrorCode
import play.api.db.DB
import java.sql.Connection
import models.dto.UserEmbryo
import org.apache.commons.codec.digest.DigestUtils
import play.api.Play.current
import models.dto.UserEmailPasswordEmbryo

case class UserRegisterParams(val email: String, val name: String, val password: String)
case class UserRegisterValues(val user: User)

object UserRegisterAPI extends AbstractAPI[UserRegisterParams, UserRegisterValues] {
  override def parseRequest(request: Request[AnyContent])(implicit context: ActionContext): UserRegisterParams = {
    val email: String = ensureFormParam("email").trim()
    val name: String = ensureFormParam("name").trim()
    val password: String = ensureFormParam("password").trim()

    return UserRegisterParams(email, name, password)
  }

  override def executeAction(params: UserRegisterParams)(implicit context: ActionContext): UserRegisterValues = {
    // Check the email has already been registered. Otherwise, we create a new user.
    // if (User.existsByEmail(email))
    DB.withConnection { implicit con: Connection =>
      if (UserEmailPassword.existsByEmail(params.email))
        throw new UserErrorControllerException(UserErrorCode.INVALID_FORM_PARAMETERS, Map("email" -> "already exists"))
    }

    val hashedPassword = DigestUtils.shaHex(params.password)

    // Register UserEmailPassword and User.
    val user: User = DB.withTransaction { implicit con: Connection =>
      val userEmbryo = new UserEmbryo(params.name)
      val userId = User.create(userEmbryo)
      val emailEmbryo = UserEmailPasswordEmbryo(userId, params.email, hashedPassword)
      UserEmailPassword.create(emailEmbryo)

      User.find(userId).get
    }

    return UserRegisterValues(user)
  }

  override def renderResult(values: UserRegisterValues)(implicit context: ActionContext): PlainResult = {
    return renderJson(values.user.toJSON())
  }
}