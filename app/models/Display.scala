package models

import play.api.libs.json.Json
import utils.Implicits._

case class Author(name: String, institute: String)

case class Display(primaryAuthor: Author, otherAuthors: Seq[Author], title: String) {
  
  def toJson = Json.toJson(this)
  
  override def toString = toJson.toString
}