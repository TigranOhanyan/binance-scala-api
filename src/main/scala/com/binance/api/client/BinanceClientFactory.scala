package com.binance.api.client

import java.io.File

import akka.actor.ActorSystem
import com.binance.api.client.constant.BinanceApiConstants
import com.binance.api.client.impl.{BinanceApiAsyncRestClientImpl, BinanceApiService, BinanceApiWebSocketClientImpl}
import com.binance.api.client.security.AuthenticationInterceptor
import com.typesafe.config.Config
import okhttp3.OkHttpClient
import retrofit2.Retrofit

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.{Duration, FiniteDuration}

/**
 * Instantiates a new binance api client factory.
 *
 * @param apiKey the API key
 * @param secret the Secret
 */
class BinanceClientFactory(
                            val apiKey: String,
                            val secret: String,
                            val simulation: Boolean,
                            val simulationPriceFile: File,
                            val simulationOrderFile: File,
                          )(implicit sys: ActorSystem, timeout: FiniteDuration) {


  /**
   * Creates a new asynchronous/non-blocking REST client.
   */
  def newAsyncRestClient: BinanceREST = if (simulation) {
    new BinanceSimulationREST(simulationPriceFile, simulationOrderFile)
  } else {
    new BinanceClientREST(apiKey, secret)
  }

  /**
   * Creates a new web socket client used for handling data streams.
   */
//  def newWebSocketClient = new BinanceApiWebSocketClientImpl
}
object BinanceClientFactory{

  def fromConfig(conf: Config)(implicit sys: ActorSystem): BinanceClientFactory = {
    val apiKey: String = conf.getString("apiKey")
    val secretKey: String = conf.getString("secretKey")
    implicit val timeout: FiniteDuration = Duration.fromNanos(conf.getDuration("timeout").toNanos)
    val simulation: Boolean = conf.getBoolean("simulation")
    val simulationPriceFile: File = new File(conf.getString("price"))
    val simulationOrderFile: File = new File(conf.getString("orders"))
    new BinanceClientFactory(apiKey, secretKey, simulation, simulationPriceFile, simulationOrderFile)
  }

}