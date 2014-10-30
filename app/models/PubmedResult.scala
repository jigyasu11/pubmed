package models

import play.api.libs.json.Json
import utils.Implicits._

case class EsearchResult(count: String, idlist: Seq[String])

case class PubmedResult(esearchresult: EsearchResult) {

  def toJson = Json.toJson(this)
  
  override def toString = toJson.toString
  
}