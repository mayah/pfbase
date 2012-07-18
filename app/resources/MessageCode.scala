package resources
import play.api.i18n.Messages

object MessageCode extends Enumeration {
  val MESSAGE_UNKNOWN = Code("message.unknown")

  val MESSAGE_AUTH_LOGIN = Code("message.auth.login")
  val MESSAGE_AUTH_LOGOUT = Code("message.auth.logout")

  case class Code(errorDescriptionId: String) extends Val(errorDescriptionId) {
    def reasonString = Messages(errorDescriptionId)
    def descriptionId = errorDescriptionId
  }
}
