package resources
import play.api.i18n.Messages

object UserErrorCode extends Enumeration {
  val INVALID_UNKNOWN = Code("invalid.unknown", 400)
  val INVALID_OAUTH_VERIFIER = Code("invalid.oauth.verifier", 400)
  val INVALID_UNEXPECTED_REQUEST = Code("invalid.unexpected", 400)

  case class Code(errorDescriptionId: String, status: Int) extends Val(errorDescriptionId) {
    def reasonString = Messages(errorDescriptionId)
    def descriptionId = errorDescriptionId
    def statusCode = status
  }
}
