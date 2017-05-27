import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.stream.scaladsl.Source
import akka.stream.{ActorMaterializer, ThrottleMode}
import com.typesafe.config._
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.slf4j.{LoggerFactory, MDC}
import play.api.libs.ws._
import play.api.libs.ws.ahc.AhcWSClient
import org.apache.commons.lang3.StringEscapeUtils

import scala.collection.JavaConversions._
import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration

object GrabOCLC extends App {

  implicit val system = ActorSystem("grab-oclc")
  implicit val materializer = ActorMaterializer()
  import system.dispatcher
  val csv = LoggerFactory.getLogger("SCRAP")
  val log = LoggerFactory.getLogger("INFO")

  val cfg = ConfigFactory.load()

  val wsClient = AhcWSClient()
  log.info("Play WS User-agent: "+cfg.getString("play.ws.useragent"))

  val savePath = s"oclc_%d-%d.json".format(cfg.getInt("oclc.start"), cfg.getInt("oclc.end"))
  log.info(s"Saving data to $savePath")
  MDC.put("savePath", savePath)
  csv.info("[\n")
  val olclRange = cfg.getInt("oclc.start") to cfg.getInt("oclc.end")
  Source(olclRange)
    .map(cfg.getString("oclc.to-string-format").format(_))
    .throttle(
      elements = cfg.getInt("oclc.throttle.elements"),
      per = {
        val per = cfg.getDuration("oclc.throttle.per")
        new FiniteDuration(per.getSeconds, TimeUnit.SECONDS)
      },
      maximumBurst = cfg.getInt("oclc.throttle.maximum-burst"),
      mode = ThrottleMode.Shaping
    )
    .mapAsync(cfg.getInt("oclc.maximum-requests")){ oclc: String =>
      val url = cfg.getString("oclc.url-template").format(oclc)
      log.debug(s"Requesting url $url")
      wsClient.url(url).get().map(oclc -> _)
    }
    .mapAsync(10){ case (oclc: String, resp: WSResponse) =>
      lazy val bibdata: Map[String, String] =
        Jsoup.parse(resp.body.toString)
          .select("div#bibdata > table > tbody > tr")
          .map { element: Element =>
            val th = StringEscapeUtils.escapeJson(element.select("th").text())
            val td = StringEscapeUtils.escapeJson(element.select("td").text())
            th -> td
          }.filter{ case (k,v) => k.nonEmpty && v.nonEmpty }.toMap
      lazy val asJSON = bibdata.updated("oclc",oclc).map{ case (k,v) => s""""${k}":"${v}""""}
        .mkString(", ")
      Future{
        log.debug(s"Data for oclc=$oclc is received.")
        s"{$asJSON},\n"
      }
    }
    .runForeach{ s: String =>
      if (s.nonEmpty){
        MDC.put("savePath", savePath)
        csv.info(s)
      }
    }.onComplete {_=>
      log.info("Finishing ...")
      MDC.put("savePath", savePath)
      csv.info("]")
      wsClient.close()
      system.terminate()
  }



//  Thread.sleep(100)

//  http://www.worldcat.org/title/125-versuche-mit-dem-oszilloskop/oclc/65092398

}
