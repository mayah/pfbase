package models.aux
import java.util.UUID

class UserId(id: UUID) extends Id(id) {
  def this(idStr: String) = this(UUID.fromString(idStr))
}
