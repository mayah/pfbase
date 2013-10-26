package app
import play.api.Configuration

object AppConfiguration {
  var toppath: String = _

  def load(conf: Configuration) = {
    toppath = conf.getString("toppath").getOrElse(throw new RuntimeException())
  }
}
