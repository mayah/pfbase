package models.dto

import java.sql.Connection
import java.util.UUID

import anorm.SQL
import anorm.sqlToSimple
import anorm.toParameterValue
import models.ids.UserId

class UserEmbryo(val screenName: String)

case class User(
    val userId: UserId,
    val screenName: String
)

object User extends ModelSupport {
  def create(id: UserId, embryo: UserEmbryo)(implicit con: Connection): User = {
    SQL("INSERT INTO Users(id, screenName) VALUES({id} :: uuid, {screenName})").on("id" -> id.toString(), "screenName" -> embryo.screenName).execute()
    new User(id, embryo.screenName)
  }

  def create(embryo: UserEmbryo)(implicit con: Connection): UserId = {
    val userId = new UserId(UUID.randomUUID())
    create(userId, embryo)
    userId
  }

  def find(userId: String)(implicit con: Connection): Option[User] = {
    try {
      val uuid = UUID.fromString(userId)
      find(new UserId(uuid))
    } catch {
      case e: IllegalArgumentException => None
    }
  }

  def find(userId: UserId)(implicit con: Connection): Option[User] = {
    SQL("SELECT * FROM Users where id = {id} :: uuid").on("id" -> userId.toString()).apply().headOption match {
      case None => None
      case Some(row) =>
        Some(new User(new UserId(row[UUID]("id")), row[String]("screenName")))
    }
  }
}
