package com.binance.api.client

import akka.Done
import akka.actor.{ActorSystem, ClassicActorSystemProvider}
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpEntity, HttpHeader, HttpMethods, HttpRequest, HttpResponse, RequestEntity, StatusCodes, Uri}
import akka.http.scaladsl.unmarshalling.{FromEntityUnmarshaller, Unmarshal, Unmarshaller}
import akka.stream.Materializer
import com.binance.api.client.domain.market.{AllBookTicker, BookTicker}
import com.binance.api.client.security.HmacSHA256Signer
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import com.binance.api.client.domain.account.NewOrder.NewFullOrder
import com.binance.api.client.domain.account.NewOrderResponse.NewOrderStdResponse
import com.binance.api.client.domain.account.NewOrder
import com.binance.api.client.domain.general.{ExchangeInfo, ServerTime}
import com.binance.api.client.exception.{BinanceApiExceptionError, BinanceApiExceptionMsg}
import com.binance.api.client.impl.RunRequest
import com.binance.api.client.marshaller.DefaultMarshalling

import scala.collection.immutable
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration.FiniteDuration

case class BinanceClientREST(
                              public: String,
                              secret: String
                            )(implicit system: ActorSystem, timeout: FiniteDuration)
  extends BinanceREST with DefaultMarshalling {


  protected val pingRequest: HttpRequest = HttpRequest(
    method = HttpMethods.GET,
    uri = BinanceClientREST.REST withPath Uri.Path("/api/v3/ping"),
  )

  protected val serverTimeRequest: HttpRequest = HttpRequest(
    method = HttpMethods.GET,
    uri = BinanceClientREST.REST withPath Uri.Path("/api/v3/time"),
  )

  protected val exchangeInfoRequest: HttpRequest = HttpRequest(
    method = HttpMethods.GET,
    uri = BinanceClientREST.REST withPath Uri.Path("/api/v3/exchangeInfo"),
  )

  protected val allBookTickerRequest: HttpRequest = HttpRequest(
    method = HttpMethods.GET,
    uri = BinanceClientREST.REST withPath Uri.Path("/api/v3/ticker/bookTicker"),
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


  /**
   * Send a new order
   *
   */
  def newOrder(order: NewFullOrder): Future[NewOrderStdResponse] = {
    val headerEndpoint: Uri = BinanceClientREST.REST withPath Uri.Path("/api/v3/order")
    val request: HttpRequest = BinanceClientREST.signRequest(headerEndpoint, order.query, None, public, secret)
    this.makeRequest[NewOrderStdResponse](request)
  }



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

object BinanceClientREST {

  val REST: Uri = Uri() withScheme "https" withHost "api.binance.com"

  type Signed = (Uri.Query, RequestEntity)

  def sign(query: Uri.Query,_entity: Option[String], secret: String): Signed = {
    val _query: String = query.toString()
    println(_query)
    val signedQuery: Uri.Query = HmacSHA256Signer(if (_query.nonEmpty) Some(_query) else None,_entity, secret) match {
      case Some(signature) =>
        val newQueries: List[(String,String)] = query.toList ::: List("signature" -> signature)
        Uri.Query(newQueries: _*)
      case _ => query
    }
    val entity: RequestEntity = _entity match {
      case Some(entity0: String) => HttpEntity(entity0)
      case None => HttpEntity.Empty
    }
    (signedQuery, entity)
  }

  def signRequest(headerEndpoint: Uri,_query: Uri.Query,_entity: Option[String], public: String, secret: String): HttpRequest = {
    val (query: Uri.Query,entity: RequestEntity) = sign(_query,_entity, secret)
    val header: HttpHeader = RawHeader("X-MBX-APIKEY", public)
    val uri: Uri = headerEndpoint withQuery query
    HttpRequest(
      uri = uri,
      method = HttpMethods.POST,
      headers = List(header),
      entity = entity
    )
  }
}