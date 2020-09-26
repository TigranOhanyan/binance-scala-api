package com.binance.api.client

import akka.Done
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpMethods, HttpRequest, StatusCodes, Uri}
import akka.http.scaladsl.unmarshalling.{FromEntityUnmarshaller, Unmarshal}
import akka.stream.Materializer
import com.binance.api.client.domain.general.{ExchangeInfo, ServerTime}
import com.binance.api.client.domain.market.AllBookTicker
import com.binance.api.client.exception.BinanceApiExceptionError
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import com.binance.api.client.marshaller.DefaultMarshalling

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration.FiniteDuration

trait BinancePublicRESTImpl extends DefaultMarshalling{

  implicit def system: ActorSystem

  implicit def timeout: FiniteDuration

  protected val pingRequest: HttpRequest = HttpRequest(
    method = HttpMethods.GET,
    uri = BinanceClientRESTImpl.REST withPath Uri.Path("/api/v3/ping"),
  )

  protected val serverTimeRequest: HttpRequest = HttpRequest(
    method = HttpMethods.GET,
    uri = BinanceClientRESTImpl.REST withPath Uri.Path("/api/v3/time"),
  )

  protected val exchangeInfoRequest: HttpRequest = HttpRequest(
    method = HttpMethods.GET,
    uri = BinanceClientRESTImpl.REST withPath Uri.Path("/api/v3/exchangeInfo"),
  )

  protected val allBookTickerRequest: HttpRequest = HttpRequest(
    method = HttpMethods.GET,
    uri = BinanceClientRESTImpl.REST withPath Uri.Path("/api/v3/ticker/bookTicker"),
  )

  /**
   * Get best price/qty on the order book for all symbols.
   */
  def getBookTickers(implicit um: FromEntityUnmarshaller[AllBookTicker]): Future[AllBookTicker] =
    this.makeRequest[AllBookTicker](allBookTickerRequest)

  def ping: Future[Done] =
    this.makeRequest[Done](pingRequest)

  def getServerTime: Future[ServerTime] =
    this.makeRequest[ServerTime](serverTimeRequest)

  def getExchangeInfo: Future[ExchangeInfo] =
    this.makeRequest[ExchangeInfo](exchangeInfoRequest)


  def makeRequest[T](request: HttpRequest)(implicit system: ActorSystem, timeout: FiniteDuration, um: FromEntityUnmarshaller[T]): Future[T] = {
    implicit val ec: ExecutionContext = system.dispatcher
    implicit val mat: Materializer = Materializer(system)
    Http().singleRequest(request).flatMap{
      case res if res.status == StatusCodes.OK =>
        system.log.info(s"${res.status.intValue()} -> ${res.status.defaultMessage()}")
        Unmarshal(res).to[T]
      case res =>
        system.log.info(s"${res.status.intValue()} -> ${res.status.defaultMessage()}")
        res.discardEntityBytes().future().flatMap( _ =>
          Future.failed(BinanceApiExceptionError(res.status.intValue(), res.status.defaultMessage()))
        )
    }
  }
}

object BinancePublicRESTImpl{

  def apply(implicit system: ActorSystem, timeout: FiniteDuration): BinancePublicRESTImpl = new BinancePublicRESTImpl{

    override implicit val system: ActorSystem = system

    override implicit val timeout: FiniteDuration = timeout
  }
}
