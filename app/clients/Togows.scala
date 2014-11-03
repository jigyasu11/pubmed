package clients

import play.api.libs.ws.WS
import play.api.Play.current
import utils.Implicits._
import play.api.libs.ws.WS
import scala.concurrent.Await
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt
import models.TogowsResult
import scala.concurrent.Future
import scala.language.postfixOps

object Togows extends TogowsTrait {
  implicit val context = scala.concurrent.ExecutionContext.Implicits.global
  
  def getDetails(pmids: Seq[String]) : List[TogowsResult] = {
    val url = URL.format(pmids.mkString(","))
    println(url)
    
    // Call pubmed and get list
    val result = WS.url(url).get().map {
      response => {
        response.json.validate[List[TogowsResult]].get
      }
    }
    Await.result(result, 60 seconds)
  }

}