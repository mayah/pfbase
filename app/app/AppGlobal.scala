package app
import play.api.GlobalSettings
import play.api.Logger
import play.api.Application
import services.TwitterService
import services.impl.TwitterServiceImpl
import play.api.Configuration

object AppGlobal extends GlobalSettings {
  var twitterService: TwitterService = null

  override def beforeStart(app: Application) = {
    Logger.debug("AppGlobal will start.")
    super.beforeStart(app)
  }

  override def onStart(app: Application) = {
    Logger.debug("AppGlobal is starting.")
    super.onStart(app)

    try {
      loadConfiguration(app.configuration)
      createServices()
      initializeServices()
    } catch {
      case e: Exception =>
        e.printStackTrace()
        throw new RuntimeException(e)
    }
  }

  private def loadConfiguration(conf: Configuration) = {
    AppConfiguration.load(conf)
  }

  private def createServices() = {
    twitterService = TwitterServiceImpl
  }

  private def initializeServices() = {
    twitterService.initialize()
  }
}
