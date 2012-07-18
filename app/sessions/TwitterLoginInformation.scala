package sessions
import twitter4j.Twitter
import twitter4j.auth.RequestToken

@serializable
class TwitterLoginInformation(val twitter: Twitter, val requestToken: RequestToken, val redirectURL: Option[String]) {
  def authenticationURL = requestToken.getAuthenticationURL()
}

