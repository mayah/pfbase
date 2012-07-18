package resources

object Constants {
  // Since session is just a cookie, we should reduce the whole quantity bytes of session value.
  object Session {
    def ID_KEY = "sessionId"
    def USER_ID_KEY = "userId"
    def TOKEN_KEY = "sessionToken"
  }

  // Flash is also just a cookie, we should reduce the whole quantify bytes of flash value.
  object Flash {
    def MESSAGE_ID = "messageId"
  }

  // Used for HTTP request parameter key.
  object Parameter {
    def SESSION_TOKEN = "sessionToken"
  }

  // Used for Cache.
  object Cache {
    def TWITTER_LOGIN_KEY_PREFIX = "twitterLogin:"
    def OPENID_LOGIN_KEY_PREFIX = "openID:"
  }
}
