package com.binance.api.client.marshaller

import akka.Done
import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.unmarshalling.{FromEntityUnmarshaller, Unmarshaller}
import akka.io.DnsExt
import akka.stream.Materializer

trait DefaultMarshalling {


  implicit def pingUnmarshaller(implicit mat: Materializer): FromEntityUnmarshaller[Done] = Unmarshaller[HttpEntity,Done](ec => ent => ent.discardBytes().future())

}
