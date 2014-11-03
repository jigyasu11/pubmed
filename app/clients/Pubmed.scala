package clients

import scala.concurrent.Future
import scala.concurrent.duration.DurationInt
import models.PubmedResult
import play.api.Play.current
import play.api.libs.ws.WS
import play.api.libs.ws.WS
import utils.Implicits._
import play.api.libs.json.JsSuccess
import play.api.libs.json.JsError
import scala.concurrent.Await
import scala.language.postfixOps


object Pubmed extends PubmedTrait {
  
  implicit val context = scala.concurrent.ExecutionContext.Implicits.global
  
  def search(str: String) : PubmedResult = {
    val url = URL.format(str)
    
    // Call pubmed and get list
    
    val result = WS.url(url).get().map {
      response => {
        response.json.validate[PubmedResult].get
      }
    }
    Await.result(result, 60 seconds)
  }
}