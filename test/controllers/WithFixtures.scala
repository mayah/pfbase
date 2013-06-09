package controllers

import org.specs2.execute.AsResult
import org.specs2.execute.Result
import org.specs2.specification.AroundExample
import models.Fixtures
import play.api.test.FakeApplication
import play.api.test.Helpers
import play.api.Logger

trait WithFixturesExample extends AroundExample {

  override def around[T: AsResult](t: => T): Result = {
    val app = FakeApplication(additionalConfiguration = Map(
      "db.default.url" -> "jdbc:postgresql:pfbase-test"
    ))

    Helpers.running(app) {
      Fixtures.prepare()
      AsResult(t)
    }
  }
}