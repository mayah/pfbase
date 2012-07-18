package models.aux
import java.util.UUID

class Id(val id: UUID) {
  def this(idStr: String) = this(UUID.fromString(idStr))

  override def toString(): String = id.toString()
}


