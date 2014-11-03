package models
import play.api.libs.json.Json
import utils.Implicits._

case class TogowsResult(authors: Seq[String], title: String, journal: String, affiliations: Seq[String]) {
  
  def toJson = Json.toJson(this)
  
  override def toString = toJson.toString
}