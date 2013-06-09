package models.ids

import java.util.UUID
import java.lang.IllegalArgumentException

class Id(val id: UUID) {
  def this(idStr: String) = this(UUID.fromString(idStr))

  override def toString(): String = id.toString()
}

object Id {
  def isValidId(idStr: String): Boolean = {
    try {
      UUID.fromString(idStr)
      true
    } catch {
      case e: IllegalArgumentException => false
    }
  }
}

case class UserId(override val id: UUID) extends Id(id) {
  def this(idStr: String) = this(UUID.fromString(idStr))
}

object UserId {
  def invalidId = new UserId(new UUID(0, 0))
}

case class UserTwitterLinkId(override val id: UUID) extends Id(id) {
  def this(idStr: String) = this(UUID.fromString(idStr))
}