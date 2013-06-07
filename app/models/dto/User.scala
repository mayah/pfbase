package models.dto

import java.sql.Connection
import java.util.UUID
import anorm.SQL
import anorm.sqlToSimple
import anorm.toParameterValue
import models.ids.UserId
import play.api.libs.json.Json
import play.api.libs.json.JsValue

case class UserEmbryo(
    val name: String
)

case class User(
    val id: UserId,
    val name: String) {

  def toJSON(): JsValue = {
    val map = Map(
      "id" -> id.toString(),
      "name" -> name
    )

    return Json.toJson(map)
  }
}

object User extends ModelSupport {
  def create(id: UserId, embryo: UserEmbryo)(implicit con: Connection): User = {
    SQL("INSERT INTO Users(id, name) VALUES({id} :: uuid, {name})").on(
        "id" -> id.toString(),
        "name" -> embryo.name
    ).execute()
    new User(id, embryo.name)
  }

  def create(embryo: UserEmbryo)(implicit con: Connection): UserId = {
    val id = new UserId(UUID.randomUUID())
    create(id, embryo)
    id
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
        Some(new User(new UserId(row[UUID]("id")), row[String]("name")))
    }
  }

  def findBy(email: String, hashedPassword: String)(implicit con: Connection): Option[User] = {
    SQL("""SELECT Users.id, Users.name FROM Users INNER JOIN UserEmailPasswords ON Users.id = UserEmailPasswords.userId
        WHERE UserEmailPasswords.email = {email} AND hashedPassword = {hashedPassword}""").on(
        "email" -> email,
        "hashedPassword" -> hashedPassword
    ).apply().headOption match {
      case None => None
      case Some(row) =>
        Some(new User(new UserId(row[UUID]("Users.id")), row[String]("Users.name")))
    }
  }
}
