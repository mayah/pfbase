package models.dto

import java.sql.Connection
import java.sql.Timestamp
import java.util.UUID
import org.joda.time.DateTime
import anorm.Row
import anorm.SQL
import anorm.sqlToSimple
import anorm.toParameterValue
import mpff.models.Id
import mpff.models.AnormModelSupport
import mpff.utils.TimeUtil
import play.api.libs.json.Json
import play.api.libs.json.Json.toJsFieldJsValueWrapper
import mpff.controllers.MPFFParserTrait

case class UserId(override val id: UUID) extends Id(id)
object UserId extends MPFFParserTrait {
  def fromString(str: String): Option[UserId] = parseUUID(str).map(UserId(_))
}

case class UserEmbryo(
  val loginId: String,
  val nickname: String,
  val email: String,
  val hashedPassword: String
)

case class User(
  val id: UserId,
  val loginId: String,
  val nickname: String,
  val email: String,
  val hashedPassword: String,
  val createdAt: DateTime
) {
  // TODO(mayah): Currently we don't have any administrator bit.
  // So, we check loginId if the user is admin or not.
  def isAdministrator(): Boolean = {
    loginId == "mayah" || loginId == "kinaba"
  }

  def toJSON() = {
    Json.obj(
      "id" -> id.toString(),
      "loginId" -> loginId,
      "nickname" -> nickname,
      "email" -> email
    )
  }
}

object User extends AnormModelSupport {
  def create(embryo: UserEmbryo)(implicit con: Connection): UserId = {
    val id = UserId(UUID.randomUUID())
    create(id, embryo)
    id
  }

  // NOTE: Use create(embryo) in usual case instead of this method.
  def create(id: UserId, embryo: UserEmbryo)(implicit con: Connection) {
    val now = TimeUtil.currentDateTime()
    SQL("INSERT INTO Users(id, loginId, nickname, email, hashedPassword, createdAt) VALUES({id} :: uuid, {loginId}, {nickname}, {email}, {hashedPassword}, {createdAt})").on(
      "id" -> id.toString(),
      "loginId" -> embryo.loginId,
      "nickname" -> embryo.nickname,
      "email" -> embryo.email,
      "hashedPassword" -> embryo.hashedPassword,
      "createdAt" -> new Timestamp(now.toDate().getTime())
    ).execute()
  }

  def exists(userId: UserId)(implicit con: Connection): Boolean = {
    SQL("SELECT 1 FROM Users WHERE id = {id} :: uuid").on(
      "id" -> userId.toString()
    ).apply().headOption match {
      case None => false
      case Some(row) => true
    }
  }

  def find(userId: UserId)(implicit con: Connection): Option[User] = {
    SQL("SELECT * FROM Users WHERE id = {id} :: uuid").on(
      "id" -> userId.toString()
    ).apply().headOption match {
      case None => None
      case Some(row) => Some(fromRow(row))
    }
  }

  def findByLoginId(loginId: String)(implicit con: Connection): Option[User] = {
    SQL("SELECT * FROM Users WHERE loginId = {loginId}").on(
      "loginId" -> loginId
    ).apply().headOption match {
      case None => None
      case Some(row) => Some(fromRow(row))
    }
  }

  def findByEmailAndPassword(email: String, hashedPassword: String)(implicit con: Connection): Option[User] = {
    SQL("SELECT * FROM Users WHERE email = {email} AND hashedPassword = {hashedPassword}").on(
      "email" -> email,
      "hashedPassword" -> hashedPassword
    ).apply().headOption match {
      case None => None
      case Some(row) => Some(fromRow(row))
    }
  }

  def existsLoginId(loginId: String)(implicit con: Connection): Boolean = {
    SQL("SELECT 1 FROM Users WHERE loginId = {loginId}").on("loginId" -> loginId).apply().headOption match {
      case None => false
      case Some(_) => true
    }
  }

  private def fromRow(row: Row): User = {
    val id = UserId(row[UUID]("id"))
    val loginId = row[String]("loginId")
    val nickname = row[String]("nickname")
    val email = row[String]("email")
    val hashedPassword = row[String]("hashedPassword")
    val createdAt = row[DateTime]("createdAt")
    User(id, loginId, nickname, email, hashedPassword, createdAt)
  }
}
