package app
import play.api.Configuration

object AppConfiguration {
  var toppath: String = _
  var twitter4jConsumerKey: String = _
  var twitter4jConsumerSecret: String = _

  def load(conf: Configuration) = {
    toppath = conf.getString("toppath").getOrElse(throw new RuntimeException())
    twitter4jConsumerKey = conf.getString("twitter4j.oauth.consumerKey").getOrElse(throw new RuntimeException())
    twitter4jConsumerSecret = conf.getString("twitter4j.oauth.consumerSecret").getOrElse(throw new RuntimeException())
  }
}
