package base

import org.joda.time.DateTime

// TimeUtil is used to obtain the current date time.
// For testing purpose, we would like to hook obtaining the current time.
object TimeUtil {
  var optionalCurrentDateTime: Option[DateTime] = None

  def resetCurrentDateTime() {
    optionalCurrentDateTime = None
  }

  def setCurrentDateTime(dateTime: DateTime) {
    optionalCurrentDateTime = Option(dateTime);
  }

  def currentDateTime(): DateTime = {
    optionalCurrentDateTime match {
      case None => new DateTime()
      case Some(x) => x
    }
  }
}