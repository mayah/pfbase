package resources
import play.api.i18n.Messages
import mpff.resources.MPFFMessageCodes
import mpff.resources.MessageCode

object MessageCodes extends MPFFMessageCodes {
  val MESSAGE_AUTH_LOGIN = MessageCode("message.login")
  val MESSAGE_AUTH_LOGOUT = MessageCode("message.logout")
}
