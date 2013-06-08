package controllers.action

import play.api.mvc.CookieBaker
import play.api.Play

case class LongLiveSession(data: Map[String, String] = Map.empty[String, String]) {
  /**
   * Optionally returns the session value associated with a key.
   */
  def get(key: String) = data.get(key)

  /**
   * Returns `true` if this session is empty.
   */
  def isEmpty: Boolean = data.isEmpty

  /**
   * Adds a value to the session, and returns a new session.
   *
   * For example:
   * {{{
   * session + ("username" -> "bob")
   * }}}
   *
   * @param kv the key-value pair to add
   * @return the modified session
   */
  def +(kv: (String, String)) = copy(data + kv)

  /**
   * Removes any value from the session.
   *
   * For example:
   * {{{
   * session - "username"
   * }}}
   *
   * @param key the key to remove
   * @return the modified session
   */
  def -(key: String) = copy(data - key)

  /**
   * Retrieves the session value which is associated with the given key.
   */
  def apply(key: String) = data(key)
}

/**
 * Helper utilities to manage the Session cookie.
 */
object LongLiveSession extends CookieBaker[LongLiveSession] {
  private val TWO_WEEKS = 60 * 60 * 24 * 14
  val COOKIE_NAME = Play.maybeApplication.flatMap(_.configuration.getString("longlivesession.cookieName")).getOrElse("PLAY_LONGLIVE_SESSION")
  val emptyCookie = new LongLiveSession
  override val isSigned = true
  override def secure = Play.maybeApplication.flatMap(_.configuration.getBoolean("longlivesession.secure")).getOrElse(false)
  override val maxAge = Play.maybeApplication.flatMap(_.configuration.getInt("longlivesession.maxAge")).orElse(Some(TWO_WEEKS))
  override val httpOnly = Play.maybeApplication.flatMap(_.configuration.getBoolean("longlivesession.httpOnly")).getOrElse(true)
  override def path = Play.maybeApplication.flatMap(_.configuration.getString("application.context")).getOrElse("/")
  override def domain = Play.maybeApplication.flatMap(_.configuration.getString("longlivesession.domain"))

  def deserialize(data: Map[String, String]) = new LongLiveSession(data)

  def serialize(session: LongLiveSession) = session.data
}