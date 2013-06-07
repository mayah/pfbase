package resources
import play.api.i18n.Messages

object ServerErrorCode extends Enumeration {
  val ERROR_UNKNOWN = Code("error.unknown", 500)
  val ERROR_DATABASE = Code("error.database", 500)
  val TWITTER_OAUTH_ERROR = Code("error.twitter.oauth", 500)

  case class Code(val descriptionId: String, val statusCode: Int) extends Val(descriptionId) {
    def description = Messages(descriptionId)
  }
}

