package controllers
import models.dto.User
import resources.MessageCode
import play.api.mvc.Request
import play.api.mvc.AnyContent

class ActionContext(val loginUser: Option[User], val request: Request[AnyContent], val sessionToken: String, val sessionId: String, val currentURL: String) {
  var redirectURL: Option[String] = None
  var messageCode: Option[MessageCode.Code] = None


  var sessionsToAddResult: List[(String, String)] = List.empty
  def shouldAddToSession(key: String, value: String): Unit =
    sessionsToAddResult = (key, value) :: sessionsToAddResult
}

