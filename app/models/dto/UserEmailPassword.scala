package models.dto

import java.sql.Connection
import java.util.UUID

import anorm.SQL
import anorm.sqlToSimple
import anorm.toParameterValue
import models.ids.UserId

case class UserEmailPasswordEmbryo(
    val userId: UserId,
    val email: String,
    val hashedPassword: String
)

case class UserEmailPassword(
    val userId: UserId,
    val email: String,
    val hashedPassword: String
)

object UserEmailPassword extends ModelSupport {
  def create(embryo: UserEmailPasswordEmbryo)(implicit con: Connection): Unit = {
    SQL("INSERT INTO UserEmailPasswords(userId, email, hashedPassword) VALUES({userId} :: uuid, {email}, {hashedPassword})").on(
        "userId" -> embryo.userId.toString(),
        "email" -> embryo.email,
        "hashedPassword" -> embryo.hashedPassword
    ).execute()
  }

  def existsByEmail(email: String)(implicit con: Connection): Boolean = {
    findByEmail(email) != None
  }

  def findByEmail(email: String)(implicit con: Connection): Option[UserEmailPassword] = {
    SQL("SELECT userId, email, hashedPassword FROM UserEmailPasswords WHERE email = {email}").on(
        "email" -> email
    ).apply().headOption match {
      case None => None
      case Some(row) =>
        Some(UserEmailPassword(new UserId(row[UUID]("userId")), row[String]("email"), row[String]("hashedPassword")))
    }
  }
}