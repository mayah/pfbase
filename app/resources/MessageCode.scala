package resources
import play.api.i18n.Messages

object MessageCode extends Enumeration {
  val MESSAGE_UNKNOWN = Code("message.unknown")
  val MESSAGE_AUTH_LOGIN = Code("message.login")
  val MESSAGE_AUTH_LOGOUT = Code("message.logout")

  case class Code(val descriptionId: String) extends Val(descriptionId) {
    def description = Messages(descriptionId)
  }
}
