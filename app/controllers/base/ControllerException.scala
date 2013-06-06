package controllers.base

import resources.ServerErrorCode
import resources.UserErrorCode

abstract class ControllerException() extends Exception {
  def maybeServerErrorCode: Option[ServerErrorCode.Code]
  def maybeUserErrorCode: Option[UserErrorCode.Code]
  def statusCode: Int
  def cause: Option[Throwable]
}

case class ServerErrorControllerException(val errorCode: ServerErrorCode.Code, val cause: Option[Throwable] = None) extends ControllerException {
  def this(ec: ServerErrorCode.Code, cause: Throwable) = this(ec, Option(cause))
  override def maybeServerErrorCode = Some(errorCode)
  override def maybeUserErrorCode = None
  override def statusCode = errorCode.statusCode
}

case class UserErrorControllerException(val errorCode: UserErrorCode.Code, val cause: Option[Throwable] = None) extends ControllerException {
  def this(ec: UserErrorCode.Code, cause: Throwable) = this(ec, Option(cause))
  override def maybeServerErrorCode = None
  override def maybeUserErrorCode = Some(errorCode)
  override def statusCode = errorCode.statusCode
}
