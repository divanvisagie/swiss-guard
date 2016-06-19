package com.swissguard

import com.swissguard.controllers.UserController
import com.swissguard.modules.DatabaseModule
import com.twitter.finatra.thrift.ThriftServer
import com.twitter.finatra.thrift.routing.ThriftRouter
import com.twitter.finatra.thrift.filters._

object SwissGuardServerMain extends ExampleServer

class ExampleServer extends ThriftServer {
  override val name = "swiss-guard"

  override def modules = Seq(DatabaseModule)

  override def configureThrift(router: ThriftRouter) {
    router
      .filter[LoggingMDCFilter]
      .filter[TraceIdMDCFilter]
      .filter[ThriftMDCFilter]
      .filter[AccessLoggingFilter]
      .filter[StatsFilter]
      .filter[ExceptionTranslationFilter]
      .add[UserController] //We can only have one in thrift
  }
}
