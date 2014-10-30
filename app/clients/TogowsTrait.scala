package clients
import models.TogowsResult
import scala.concurrent.Future

trait TogowsTrait {

  val URL = "http://togows.dbcls.jp/entry/ncbi-pubmed/%s.json"
  def getDetails(pmids: Seq[String]) : List[TogowsResult]
}