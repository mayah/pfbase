package resources
import play.api.i18n.Messages

object UserErrorCode extends Enumeration {
  val INVALID_UNKNOWN = Code("invalid.unknown", 400)

  val INVALID_AUTH_PASSWORD = Code("invalid.auth.password", 400)

  // These errors are specially used for form parameter.
  val INVALID_FORM_PARAMETERS = Code("invalid.form.parameters", 400)

  val INVALID_OAUTH_VERIFIER = Code("invalid.oauth.verifier", 400)
  val INVALID_UNEXPECTED_REQUEST = Code("invalid.unexpected", 400)

  case class Code(val descriptionId: String, val statusCode: Int) extends Val(descriptionId) {
    def description = Messages(descriptionId)
  }
}
