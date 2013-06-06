package sessions
import twitter4j.Twitter
import twitter4j.auth.RequestToken

class TwitterLoginInformation(
    val twitter: Twitter,
    val requestToken: RequestToken,
    val redirectURL: Option[String]) extends Serializable {
  def authenticationURL = requestToken.getAuthenticationURL()
}

