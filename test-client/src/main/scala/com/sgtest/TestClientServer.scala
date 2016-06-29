package com.sgtest

import java.net.InetSocketAddress

import com.sgtest.controllers.AuthenticationController
import com.twitter.finagle.Http
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.stats.DefaultStatsReceiver
import com.twitter.finagle.zipkin.thrift.ZipkinTracer
import com.twitter.finatra.http.HttpServer
import com.twitter.finatra.http.filters.{CommonFilters, LoggingMDCFilter, TraceIdMDCFilter}
import com.twitter.finatra.http.routing.HttpRouter


object TestClientServerMain extends TestClientServer

class TestClientServer extends HttpServer {

  override def defaultFinatraHttpPort = ":8888"

  override val adminPort = flag("admin.port", new InetSocketAddress(8880), "Admin Http server port")

  override def configureHttp(router: HttpRouter) {
    router
      .filter[LoggingMDCFilter[Request, Response]]
      .filter[TraceIdMDCFilter[Request, Response]]
      .filter[CommonFilters]
      .add[AuthenticationController]
  }

  override def configureHttpServer(server: Http.Server): Http.Server = {
    val receiver = DefaultStatsReceiver.get
    val tracer = ZipkinTracer.mk(
      host = "localhost",
      port = 9410,
      statsReceiver = receiver,
      sampleRate = 1.0f
    )
    server
        .withTracer(tracer)
  }

}