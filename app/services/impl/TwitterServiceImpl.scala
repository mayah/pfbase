package services.impl
import services.TwitterService
import twitter4j.conf.Configuration
import twitter4j.conf.ConfigurationBuilder
import sessions.TwitterLoginInformation
import twitter4j.TwitterFactory
import app.AppConfiguration
import sessions.TwitterLoginInformation
import models.dto.UserTwitterLinkEmbryo
import twitter4j.auth.AccessToken

object TwitterServiceImpl extends TwitterService {
  private var conf: Configuration = null

  def initialize() = {
    val builder = new ConfigurationBuilder()

    builder.setOAuthConsumerKey(AppConfiguration.twitter4jConsumerKey)
    builder.setOAuthConsumerSecret(AppConfiguration.twitter4jConsumerSecret)

    conf = builder.build()
  }

  override def createLoginInformation(redirectURL: Option[String]): TwitterLoginInformation = {
    val twitter = new TwitterFactory(conf).getInstance()
    val callbackURL = AppConfiguration.toppath + "/auth/verifyForTwitter"
    val requestToken = twitter.getOAuthRequestToken(callbackURL)

    new TwitterLoginInformation(twitter, requestToken, redirectURL)
  }

  override def createTwitterLinkFromLoginInformation(info: TwitterLoginInformation, verifier: String): UserTwitterLinkEmbryo = {
    val twitter = info.twitter
    val requestToken = info.requestToken
    val accessToken = twitter.getOAuthAccessToken(requestToken, verifier)

    val twitterUser = twitter.showUser(twitter.getId())
    new UserTwitterLinkEmbryo(
        twitter.getId(),
        twitterUser.getScreenName(),
        twitterUser.getName(),
        twitterUser.getProfileImageURL().toString(),
        Option(accessToken.getToken()),
        Option(accessToken.getTokenSecret()))
  }

  override def sendDirectMessage(token: String, tokenSecret: String, twitterId: Long, message: String) = {
    val accessToken = new AccessToken(token, tokenSecret)
    val twitter = new TwitterFactory(conf).getInstance(accessToken);
    twitter.sendDirectMessage(twitterId, message);
  }

  override def updateStatus(token: String, tokenSecret: String, message: String) = {
    val accessToken = new AccessToken(token, tokenSecret)
    val twitter = new TwitterFactory(conf).getInstance(accessToken);
    twitter.updateStatus(message)
  }
}

