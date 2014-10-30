package clients

import models.PubmedResult
import scala.concurrent.Future

trait PubmedTrait {
  
  val URL = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=pubmed&term=%s&retmax=200&retmode=json"
  
  def search(str: String) : PubmedResult
}