package controllers.user

import org.specs2.mutable.Specification

import controllers.WithFixturesExample
import controllers.api.user.UserShowAPI
import models.Fixtures
import play.api.libs.json.JsString
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers
import play.api.test.Helpers.GET
import play.api.test.Helpers.contentAsString
import play.api.test.Helpers.route
import play.api.test.Helpers.writeableOf_AnyContentAsEmpty


class UserShowSpec extends Specification with WithFixturesExample {
  def API_PATH = "/api/users"

  "get /api/users" should {
    "map to UserShowAPI" in {
      val request = FakeRequest(GET, API_PATH)
      val result = route(request)

      result must not be None
    }
  }

  "get /api/users?userId=xxx" should {
    "return an appropriate user" in {
      val request = FakeRequest(GET, API_PATH + "?userId=" + Fixtures.User1.id.toString)
      val result = UserShowAPI.run(request)

      val json: JsValue = Json.parse(contentAsString(result))
      (json \ "name").asInstanceOf[JsString].value must equalTo(Fixtures.User1.name)
    }

    "return 404 Not Found if not found" in {
      val request = FakeRequest(GET, API_PATH + "?userId=" + Fixtures.InvalidUser.id.toString)
      val result = UserShowAPI.run(request)

      Helpers.status(result) must equalTo(404)
    }

    "return 400 Bad Request if userId is not specified" in {
      val request = FakeRequest(GET, API_PATH)
      val result = UserShowAPI.run(request)

      Helpers.status(result) must equalTo(400)
    }
  }
}