package controllers

import models.dto.User
import play.api.mvc.AnyContent
import play.api.mvc.Request
import resources.MessageCode

/** ActionContext contains several information per a user request.
 */
class ActionContext(
    val maybeLoginUser: Option[User],   // Some(User) if a user is logged in, None otherwise
    val request: Request[AnyContent],   // Request object
    val sessionToken: String,
    val sessionId: String,
    val currentURL: String) {
  var redirectURL: Option[String] = None
  var messageCode: Option[MessageCode.Code] = None

  var sessionsToAddResult: List[(String, String)] = List.empty
  def shouldAddToSession(key: String, value: String) {
    sessionsToAddResult = (key, value) :: sessionsToAddResult
  }

  var headers: List[(String, String)] = List.empty
  def addHeader(key: String, value: String) {
    headers = (key -> value) :: headers
  }
}

