package com.binance.api.client

import akka.actor.ActorSystem
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.RawHeader
import com.binance.api.client.domain.account.NewOrder.NewFullOrder
import com.binance.api.client.domain.account.NewOrderResponse.NewOrderStdResponse
import com.binance.api.client.security.HmacSHA256Signer

import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration

case class BinanceClientRESTImpl(
                              public: String,
                              secret: String
                            )(implicit override val system: ActorSystem, override val timeout: FiniteDuration)
  extends BinanceClientREST with BinancePublicRESTImpl {


  /**
   * Send a new order
   *
   */
  def newOrder(order: NewFullOrder): Future[NewOrderStdResponse] = {
    val headerEndpoint: Uri = BinanceClientRESTImpl.REST withPath Uri.Path("/api/v3/order")
    val request: HttpRequest = BinanceClientRESTImpl.signRequest(headerEndpoint, order.query, None, public, secret)
    this.makeRequest[NewOrderStdResponse](request)
  }

}

object BinanceClientRESTImpl {

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