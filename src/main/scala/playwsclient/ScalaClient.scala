package playwsclient

import java.io.{BufferedReader, InputStreamReader}
import java.net.URL

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import play.api.libs.ws._
import play.api.libs.ws.ahc._

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.Future

object ScalaClient {
  import DefaultBodyReadables._

  import scala.concurrent.ExecutionContext.Implicits._

  val config = ConfigFactory.load();
  val userName = config.getString("userName")
  val password = config.getString("password")

  def main(args: Array[String]): Unit = {

    stock
    news
    rFoo
  }

  private var STOCK = "AAPL"

  def stock = {
    // https://support.klipfolio.com/hc/en-us/articles/215546368-Use-Yahoo-Finance-as-a-data-source-
    val url = new URL("http://download.finance.yahoo.com/d/quotes.csv?s=" + STOCK + "&f=nsl1opc1p2&e=.csv")
    val in = new BufferedReader(new InputStreamReader(url.openStream))
    val buffer = new ArrayBuffer[String]()
    var output = ""
    var inputLine = in.readLine
    while (inputLine != null) {
      if (!inputLine.trim.equals("")) {
        buffer += inputLine.trim
        output += inputLine.trim
      }
      inputLine = in.readLine
    }
    in.close

    buffer.foreach(println)
    println("done")
  }

  def news = {
    // Create Akka system for thread and streaming management
    implicit val system = ActorSystem()
    system.registerOnTermination {
      System.exit(0)
    }
    implicit val materializer = ActorMaterializer()
    val wsClient = StandaloneAhcWSClient()
    call(wsClient)
      .andThen { case _ => wsClient.close() }
      .andThen { case _ => system.terminate() }
  }

  private def call(ws: StandaloneWSClient): Future[Unit] = {
    ws.url("https://api.intrinio.com/companies")
        .addQueryStringParameters("ticker" -> STOCK)
      .withAuth(userName,password, WSAuthScheme.BASIC)
      .get().map { response ⇒
      val statusText: String = response.statusText
      val body = response.body[String]
      println(s"Got a response $statusText")
      println(body)
      println("done")
    }
  }

  def rFoo () {
    val R = org.ddahl.rscala.RClient()

    val a = R.evalD0("rnorm(8)")
    val b = R.evalD1("rnorm(8)")
    val c = R.evalD2("matrix(rnorm(8),nrow=4)")

    R.set("ages", Array(4,2,7,9))
    R.ages = Array(4,2,7,9)
    println(R.getI1("ages").mkString("<",", ",">"))

    R eval  """
            v <- rbinom(8,size=10,prob=0.4)
            m <- matrix(v,nrow=4)
            """

    val v1 = R.get("v")
    val v2 = R.get("v")._1.asInstanceOf[Array[Int]]   // This works, but is not very convenient
    val v3 = R.v._1.asInstanceOf[Array[Int]]          // Slightly better
    val v4 = R.getI0("v")   // Get the first element of R's "v" as a Int
    val v5 = R.getI1("v")   // Get R's "v" as an Array[Int]
    val v6 = R.getI2("m")   // Get R's "m" as an Array[Array[Int]]

    println("done");
  }

}
