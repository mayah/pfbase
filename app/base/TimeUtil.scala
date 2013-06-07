package base

import org.joda.time.DateTime

// TimeUtil is used to obtain the current date time.
// For testing purpose, we would like to hook obtaining the current date time.
object TimeUtil {
  var maybeCurrentDateTime: Option[DateTime] = None

  def resetCurrentDateTime() {
    maybeCurrentDateTime = None
  }

  def setCurrentDateTime(dateTime: DateTime) {
    maybeCurrentDateTime = Option(dateTime);
  }

  def currentDateTime(): DateTime = {
    maybeCurrentDateTime match {
      case None => new DateTime()
      case Some(x) => x
    }
  }
}