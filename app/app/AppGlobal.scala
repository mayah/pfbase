package app

import play.api.GlobalSettings
import play.api.Logger
import play.api.Application
import play.api.Configuration

object AppGlobal extends GlobalSettings {

  override def beforeStart(app: Application) = {
    Logger.debug("AppGlobal will start.")
    super.beforeStart(app)
  }

  override def onStart(app: Application) = {
    Logger.debug("AppGlobal is starting.")
    super.onStart(app)

    try {
      loadConfiguration(app.configuration)
    } catch {
      case e: Exception =>
        e.printStackTrace()
        throw new RuntimeException(e)
    }
  }

  private def loadConfiguration(conf: Configuration) = {
    AppConfiguration.load(conf)
  }
}
