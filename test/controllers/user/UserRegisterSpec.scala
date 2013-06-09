package controllers.user

import org.specs2.mutable.Specification
import controllers.WithFixturesExample
import play.api.test.FakeRequest
import play.api.test.Helpers.GET
import play.api.test.Helpers.POST
import controllers.api.user.UserRegisterAPI
import play.api.test.Helpers
import play.api.test.Helpers.route
import play.api.test.Helpers.writeableOf_AnyContentAsEmpty
import play.api.db.DB
import models.dto.User
import java.sql.Connection
import play.api.Play.current
import org.apache.commons.codec.digest.DigestUtils

class UserRegisterSpec extends Specification with WithFixturesExample {
  def API_PATH = "/api/users/register"

  "api" should {
    "be accessible with POST method" in {
      val request = FakeRequest(POST, API_PATH)
      val result = route(request)

      result must not be None
    }

    "not be accessible with GET method" in {
      val request = FakeRequest(GET, API_PATH)
      val result = route(request)

      result must be(None)
    }
  }

  "user" should {
    "be able to register with email, name, and password" in {
      val request = FakeRequest(POST, API_PATH).withFormUrlEncodedBody(
        "email" -> "test@example.com",
        "name" -> "test name",
        "password" -> "password"
      )
      val result = UserRegisterAPI.run(request)

      Helpers.status(result) must equalTo(200)

      val optionalUser = DB.withConnection { implicit con: Connection =>
        val hashedPassword = DigestUtils.shaHex("password")
        User.findBy("test@example.com", hashedPassword)
      }
      optionalUser.get.name must equalTo("test name")
    }
  }

  "when registering, prefix spaces and trailing spaces" should {
    "be removed" in {
      val request = FakeRequest(POST, API_PATH).withFormUrlEncodedBody(
        "email" -> "   test@example.com   ",
        "name" -> "   test name   ",
        "password" -> "   password   "
      )
      val result = UserRegisterAPI.run(request)

      Helpers.status(result) must equalTo(200)

      val optionalUser = DB.withConnection { implicit con: Connection =>
        val hashedPassword = DigestUtils.shaHex("password")
        User.findBy("test@example.com", hashedPassword)
      }
      optionalUser.get.name must equalTo("test name")
    }
  }
}
