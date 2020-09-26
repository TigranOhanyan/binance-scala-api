package simulation

import java.io.{BufferedWriter, File, FileWriter}

import com.typesafe.config.{Config, ConfigFactory}

import scala.annotation.tailrec
import scala.util.Try

object SimulatePrice extends App{

  val conf: Config = ConfigFactory.load()
  val simConf: Config = conf.getConfig("simulation")
  val minRange: BigDecimal = BigDecimal(simConf.getDouble("min"))
  val maxRange: BigDecimal = BigDecimal(simConf.getDouble("max"))
  val start: BigDecimal = (BigDecimal(simConf.getDouble("start")) min maxRange) max minRange
  val end: BigDecimal = (BigDecimal(simConf.getDouble("end")) min maxRange) max minRange
  val up: Boolean = simConf.getBoolean("up")
  val wave: Int = simConf.getInt("wave")
  require(wave >= 0)
  val n0: Int = simConf.getInt("n")
  val n: Int = 2 * wave * n0
  val file: File = new File(simConf.getString("file"))
  file.createNewFile()

  val range: BigDecimal = maxRange - minRange
  val step: BigDecimal = range / n0


  val bw: BufferedWriter = new BufferedWriter(new FileWriter(file))
  @tailrec
  def gen(last: BigDecimal, index: Int, up: Boolean): Unit = {
    val line: String = s"$last"
    Try{
      bw.write(line)
      bw.write("\n")
    }
    if (index <= n){
      val pre: BigDecimal = if (up) last + step else last - step
      val invalid: Boolean = if (up) pre > maxRange else pre < minRange
      val _last: BigDecimal = if (invalid && up) {
        maxRange - (pre - maxRange)
      } else if (invalid && !up) {
        minRange + minRange - pre
      } else pre
      val _up: Boolean = if (invalid) !up else up
      gen(_last, index + 1, _up)
    }
  }

  gen(start, 1, up)

  bw.close()
}
