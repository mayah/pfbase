package controllers

import java.sql.Connection
import java.util.UUID

import models.dto.User
import models.dto.UserId
import mpff.controllers.MPFFActionContext
import mpff.controllers.MPFFParserTrait
import mpff.sessions.LongLiveSession
import play.api.Play.current
import play.api.db.DB
import play.api.mvc.AnyContent
import play.api.mvc.Request
import resources.Constants

/** ActionContext contains several information per a user request.
 */
class ActionContext(
    val optLoginUser: Option[User],   // Some(User) if a user is logged in, None otherwise
    override val request: Request[AnyContent],   // Request object
    override val sessionToken: String,
    override val sessionId: String) extends MPFFActionContext(request, sessionToken, sessionId) {
}

trait ActionContextPreparer extends MPFFParserTrait {
  def prepareActionContext(request: Request[AnyContent]): ActionContext = {
    val userFromSession: Option[User] = optUserFromSession(request)
    val userFromLongLiveSession: Option[User] = optUserFromLongLiveSession(request)

    val sessionToken: String = request.session.get(Constants.Session.TOKEN_KEY) match {
      case None => UUID.randomUUID().toString()
      case Some(x) => x
    }

    val sessionId: String = request.session.get(Constants.Session.ID_KEY) match {
      case None => UUID.randomUUID().toString()
      case Some(x) => x
    }

    val context = new ActionContext(userFromSession.orElse(userFromLongLiveSession), request, sessionToken, sessionId)

    // --- prepare session values
    context.addSessionValue(Constants.Session.TOKEN_KEY, sessionToken)
    context.addSessionValue(Constants.Session.ID_KEY, sessionId)
    if (userFromSession != None) {
        context.addSessionValue(Constants.Session.USER_ID_KEY, userFromSession.get.id.toString)
    } else if (userFromLongLiveSession != None) {
        context.addLongLiveSessionValue(Constants.Session.USER_ID_KEY, userFromLongLiveSession.get.id.toString)
    }

    return context
  }

  private def optUserFromSession(request: Request[AnyContent]): Option[User] = {
    request.session.get(Constants.Session.USER_ID_KEY).flatMap(parseUUID(_)).flatMap { case userId =>
      DB.withConnection { implicit con: Connection =>
        User.find(UserId(userId))
      }
    }
  }

  private def optUserFromLongLiveSession(request: Request[AnyContent]): Option[User] = {
    // We have to decode long live session by ourselves.
    val longliveSession = LongLiveSession.decodeFromCookie(request.cookies.get(LongLiveSession.COOKIE_NAME))
    longliveSession.get(Constants.Session.USER_ID_KEY).flatMap(parseUUID(_)).flatMap { case userId =>
      DB.withConnection { implicit con: Connection =>
        User.find(UserId(userId))
      }
    }
  }
}
