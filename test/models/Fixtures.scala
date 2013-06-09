package models

import play.api.db.DB
import java.sql.Connection
import play.api.Play.current
import models.dto.User
import models.dto.UserEmailPassword
import models.dto.UserEmbryo
import models.ids.UserId
import java.util.UUID

object Fixtures {
  object InvalidUser {
    val id: UserId = new UserId(new UUID(0, 0))
    val name: String = "invalid user"
  }

  object User1 {
    var instance: User =_
    var id: UserId     = _
    val name: String   = "user 1"
  }

  def prepare() {
    DB.withConnection { implicit con: Connection =>
      deleteAll()
      prepareUser()
    }
  }

  def prepareUser()(implicit con: Connection) {
    User1.id = User.create(UserEmbryo(User1.name))
    User1.instance = User.find(User1.id).get
  }

  def deleteAll()(implicit con: Connection) {
    User.deleteAll()
    UserEmailPassword.deleteAll()
  }
}