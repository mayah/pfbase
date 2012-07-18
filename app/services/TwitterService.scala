package services
import twitter4j.TwitterException
import sessions.TwitterLoginInformation
import models.dto.UserTwitterLink
import models.dto.UserTwitterLinkEmbryo

trait TwitterService {
  def initialize()
  def createLoginInformation(redirectURL: Option[String]): TwitterLoginInformation
  def createTwitterLinkFromLoginInformation(info: TwitterLoginInformation, verifier: String): UserTwitterLinkEmbryo
  def updateStatus(token: String, tokenSecret: String, message: String)
  def sendDirectMessage(token: String, tokenSecret: String, twitterId: Long, message: String)
}

