package models.ids

import java.util.UUID

class Id(val id: UUID) {
  def this(idStr: String) = this(UUID.fromString(idStr))

  override def toString(): String = id.toString()
}

class UserId(id: UUID) extends Id(id) {
  def this(idStr: String) = this(UUID.fromString(idStr))
}

class UserTwitterLinkId(id: UUID) extends Id(id) {
  def this(idStr: String) = this(UUID.fromString(idStr))
}