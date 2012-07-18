package models.aux
import java.util.UUID

class UserTwitterLinkId(id: UUID) extends Id(id) {
  def this(idStr: String) = this(UUID.fromString(idStr))
}
