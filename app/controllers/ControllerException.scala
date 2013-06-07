package controllers

import resources.ServerErrorCode
import resources.UserErrorCode

abstract class ControllerException() extends Exception {
  def maybeServerErrorCode: Option[ServerErrorCode.Code]
  def maybeUserErrorCode: Option[UserErrorCode.Code]
  def statusCode: Int
  def optionalCause: Option[Throwable]
}

case class ServerErrorControllerException(
    val errorCode: ServerErrorCode.Code,
    val optionalCause: Option[Throwable] = None,
    val optionalInfo: Option[Map[String, String]] = None) extends ControllerException {
  def this(ec: ServerErrorCode.Code, cause: Throwable) = this(ec, Option(cause))
  def this(ec: ServerErrorCode.Code, info: Map[String, String]) = this(ec, None, Option(info))
  def this(ec: ServerErrorCode.Code, cause: Throwable, info: Map[String, String]) = this(ec, Option(cause), Option(info))
  override def maybeServerErrorCode = Some(errorCode)
  override def maybeUserErrorCode = None
  override def statusCode = errorCode.statusCode
}

case class UserErrorControllerException(
    val errorCode: UserErrorCode.Code,
    val optionalCause: Option[Throwable] = None,
    val optionalInfo: Option[Map[String, String]] = None) extends ControllerException {
  def this(ec: UserErrorCode.Code, cause: Throwable) = this(ec, Option(cause))
  def this(ec: UserErrorCode.Code, info: Map[String, String]) = this(ec, None, Option(info))
  def this(ec: UserErrorCode.Code, cause: Throwable, info: Map[String, String]) = this(ec, Option(cause), Option(info))
  override def maybeServerErrorCode = None
  override def maybeUserErrorCode = Some(errorCode)
  override def statusCode = errorCode.statusCode
}
