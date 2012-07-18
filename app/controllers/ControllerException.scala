package controllers
import resources.UserErrorCode
import resources.ServerErrorCode

abstract class ControllerException() extends Exception {
  def serverErrorCode: Option[ServerErrorCode.Code]
  def userErrorCode: Option[UserErrorCode.Code]
  def statusCode: Int
  def cause: Option[Throwable]
}

class ServerErrorControllerException(ec: ServerErrorCode.Code, val cause: Option[Throwable] = None) extends ControllerException {
  def this(ec: ServerErrorCode.Code, cause: Throwable) = this(ec, Option(cause))
  override def serverErrorCode = Some(ec)
  override def userErrorCode = None
  override def statusCode = ec.statusCode
}

class UserErrorControllerException(ec: UserErrorCode.Code, val cause: Option[Throwable] = None) extends ControllerException {
  def this(ec: UserErrorCode.Code, cause: Throwable) = this(ec, Option(cause))
  override def serverErrorCode = None
  override def userErrorCode = Some(ec)
  override def statusCode = ec.statusCode
}
