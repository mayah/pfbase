package controllers

import models.dto.User
import play.api.mvc.AnyContent
import play.api.mvc.Request
import resources.MessageCode
import resources.UserErrorCode
import resources.ServerErrorCode
import play.api.mvc.Cookie

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

  // ----------------------------------------------------------------------

  var sessionValues: List[(String, String)] = List.empty
  def addSessionValue(key: String, value: String) {
    sessionValues = (key, value) :: sessionValues
  }
  def discardSession() {
    sessionValues = List.empty
  }

  var longliveSessionValues: List[(String, String)] = List.empty
  def addLongLiveSessionValue(key: String, value: String) {
    longliveSessionValues = (key, value) :: longliveSessionValues
  }
  def discardLongLiveSession() {
    longliveSessionValues = List.empty
  }

  var flashingValues: List[(String, String)] = List.empty
  def addFlashing(key: String, value: String) {
    flashingValues = (key, value) :: flashingValues
  }

  var headers: List[(String, String)] = List.empty
  def addHeader(key: String, value: String) {
    headers = (key -> value) :: headers
  }
}

