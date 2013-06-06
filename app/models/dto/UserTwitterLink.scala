package models.dto

import java.sql.Connection
import java.util.UUID

import anorm.SQL
import anorm.sqlToSimple
import anorm.toParameterValue
import models.ids.UserId
import models.ids.UserTwitterLinkId

class UserTwitterLinkEmbryo(
    val twitterId: Long,
    val screenName: String,
    val name: String,
    val profileImageURL: String,
    val accessToken: Option[String],
    val accessTokenSecret: Option[String]
)

case class UserTwitterLink(
    val id: UserTwitterLinkId,
    val userId: UserId,
    val twitterId: Long,
    val screenName: String,
    val name: String,
    val profileImageURL: String,
    val accessToken: Option[String],
    val accessTokenSecret: Option[String]
)

object UserTwitterLink extends ModelSupport {

  def findByTwitterId(twitterId: Long)(implicit con: Connection): Option[UserTwitterLink] = {
    SQL("SELECT * FROM UserTwitterLinks where twitterId = {twitterId}").on("twitterId" -> twitterId).apply().headOption match {
      case None => None
      case Some(row) =>
        Some(new UserTwitterLink(
            new UserTwitterLinkId(row[UUID]("id")),
            new UserId(row[UUID]("userId")),
            row[Long]("twitterId"),
            row[String]("screenName"),
            row[String]("name"),
            row[String]("profileImageURL"),
            row[Option[String]]("accessToken"),
            row[Option[String]]("accessTokenSecret")
        ))
    }
  }

  def create(embryo: UserTwitterLinkEmbryo, userId: UserId)(implicit con: Connection): UserTwitterLinkId = {
    val id = new UserTwitterLinkId(UUID.randomUUID())
    SQL("""INSERT INTO UserTwitterLinks(id, userId, twitterId, screenName, name, profileImageURL, accessToken, accessTokenSecret)
        VALUES({id} :: uuid, {userId} :: uuid, {twitterId}, {screenName}, {name}, {profileImageURL}, {accessToken}, {accessTokenSecret})"""
    ).on(
        "id" -> id.toString(),
        "userId" -> userId.toString(),
        "twitterId" -> embryo.twitterId,
        "screenName" -> embryo.screenName,
        "name" -> embryo.name,
        "profileImageURL" -> embryo.profileImageURL,
        "accessToken" -> embryo.accessToken,
        "accessTokenSecret" -> embryo.accessTokenSecret
    ).execute()
    id
  }

}


