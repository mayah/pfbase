package controllers

import models.dto.User
import play.api.mvc.AnyContent
import play.api.mvc.Request
import resources.MessageCode
import resources.UserErrorCode
import resources.ServerErrorCode

/** ActionContext contains several information per a user request.
 */
class ActionContext(
    val maybeLoginUser: Option[User],   // Some(User) if a user is logged in, None otherwise
    val request: Request[AnyContent],   // Request object
    val sessionToken: String,
    val sessionId: String,
    val currentURL: String) {
  var maybeRedirectURL: Option[String] = None

  var messageCodes: List[MessageCode.Code] = List.empty
  def addMessageCode(messageCode: MessageCode.Code) {
    messageCodes = messageCode :: messageCodes
  }

  var userErrorCodes: List[UserErrorCode.Code] = List.empty
  def addUserErrorCode(userErrorCode: UserErrorCode.Code) {
    userErrorCodes = userErrorCode :: userErrorCodes
  }

  var serverErrorCodes: List[ServerErrorCode.Code] = List.empty
  def addServerErrorCode(serverErrorCode: ServerErrorCode.Code) {
    serverErrorCodes = serverErrorCode :: serverErrorCodes
  }

  def hasSomeMessages(): Boolean = {
    return messageCodes != List.empty || userErrorCodes != List.empty || serverErrorCodes != List.empty
  }

  var sessionsToAddResult: List[(String, String)] = List.empty
  def shouldAddToSession(key: String, value: String) {
    sessionsToAddResult = (key, value) :: sessionsToAddResult
  }

  var headers: List[(String, String)] = List.empty
  def addHeader(key: String, value: String) {
    headers = (key -> value) :: headers
  }
}

