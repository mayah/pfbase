package models.dto
import java.util.UUID

import anorm.MayErr.eitherToError
import anorm.Column
import anorm.MetaDataItem
import anorm.TypeDoesNotMatch

/**
 * ModelSupport provides a few utility methods.
 */
trait ModelSupport {
  implicit def rowToUUID: Column[UUID] = {
    Column.nonNull[UUID] { (value, meta) =>
      val MetaDataItem(qualified, nullable, clazz) = meta
      value match {
        case uuid: UUID => Right(uuid)
        case _ => Left(TypeDoesNotMatch("Cannot convert " + value + ":" + value.asInstanceOf[AnyRef].getClass + " to UUID for column " + qualified))
      }
    }
  }
}
