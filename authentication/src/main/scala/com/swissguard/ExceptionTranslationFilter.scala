package com.swissguard

import java.lang.Exception

import com.twitter.finagle.{Service, TimeoutException}
import com.twitter.finatra.thrift.thriftscala.ClientErrorCause.RequestTimeout
import com.twitter.finatra.thrift.thriftscala.ServerErrorCause.InternalServerError
import com.twitter.finatra.thrift.thriftscala.{ClientError, ServerError}
import com.twitter.finatra.thrift.{ThriftFilter, ThriftRequest}
import com.twitter.inject.Logging
import com.twitter.util.{Future, NonFatal}
import javax.inject.Singleton

@Singleton
class ExceptionTranslationFilter
  extends ThriftFilter
  with Logging {

  override def apply[T, U](request: ThriftRequest[T], service: Service[ThriftRequest[T], U]): Future[U] = {
    service(request).rescue {
      case e: TimeoutException =>
        Future.exception(
          ClientError(RequestTimeout, e.getMessage))
      case e: ClientError =>
        Future.exception(e)
      case NonFatal(e) =>
        error("Unhandled exception", e)
        Future.exception(
          ServerError(InternalServerError, e.getMessage))
      case e: Exception =>
        error("Unhandled exception", e)
        Future.exception(
          ServerError(InternalServerError, e.getMessage))
    }
  }
}
