package utils

import models._
import play.api.libs.json.Json
import models.TogowsResult

object Implicits {
  
  implicit val esearchResultFormatter = Json.format[EsearchResult]
  
  implicit val pubmedResultFormatter = Json.format[PubmedResult]
  
  implicit val authorFormatter = Json.format[Author]
  
  implicit val displayFormatter = Json.format[Display]
    
  implicit val togowsResultFormatter = Json.format[TogowsResult]
  
}