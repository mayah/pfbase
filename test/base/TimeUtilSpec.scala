package base

import org.joda.time.DateTime
import org.specs2.mutable.Specification
import org.specs2.specification.AfterExample
import org.specs2.specification.BeforeExample
import org.specs2.specification.BeforeAfterExample

class TimeUtilSpec extends Specification with BeforeAfterExample {
  def before = {
    TimeUtil.resetCurrentDateTime()
  }

  def after = {
    TimeUtil.resetCurrentDateTime()
  }

  "TimeUtil optionalCurrentDateTime" should {
    "be None at first" in {
      TimeUtil.optionalCurrentDateTime must equalTo(None)
    }
  }

  "TimeUtil currentDateTime" should {
    "return the set time after setting time" in {
      val dateTime = new DateTime(10)
      TimeUtil.setCurrentDateTime(dateTime)

      TimeUtil.currentDateTime must equalTo(dateTime)
      TimeUtil.optionalCurrentDateTime must equalTo(Some(dateTime))
    }

    "forget set time after reset" in {
      TimeUtil.setCurrentDateTime(new DateTime(10))
      TimeUtil.resetCurrentDateTime()

      TimeUtil.optionalCurrentDateTime must equalTo(None)
    }

    "overrwrite previously set time" in {
      TimeUtil.setCurrentDateTime(new DateTime(10))
      TimeUtil.setCurrentDateTime(new DateTime(20))

      TimeUtil.currentDateTime must equalTo(new DateTime(20))
    }
  }
}
