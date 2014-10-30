package controllers

import play.api._
import play.api.mvc._
import models.Display
import clients.Pubmed
import clients.Togows
import play.api.libs.json.Json
import models._
import utils.Implicits._
import scala.collection.mutable.MutableList
import scala.util.Failure
import scala.util.Success
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object Application extends Controller {

	def index = Action {
		Ok(views.html.index("Your new application is ready."))
	}

	def search(term: String, limit: Int) = Action {
		try {
			println("I AM HERE " + limit)
		  val formattedStr = term.replaceAll("\\s+", "+")
		  val res = performSearch(formattedStr, limit)
		  Ok(Json.toJson(res))
		} catch {
			case e => InternalServerError("Something bad hapened " + e)
		}
	}

	def performSearch(searchStr: String, limit: Int) : List[Display] = {
	  val pmids = Pubmed.search(searchStr).esearchresult.idlist
	  if (limit>0) {
	    queryTogows(pmids.take(limit))
	  } else {
	    queryTogows(pmids)	  	
	  }
	}
	
	def queryTogows(pmids: Seq[String]) : List[Display] = {
		val result = for {
			res <- Togows.getDetails(pmids)
		} yield {
			var authors : Seq[Author] = Nil
	    var primaryAuthor : Author = null
	    if (res.affiliations.size > 0) {
		    if (res.affiliations.size == 1) {
			    authors = res.authors.map(x => Author(x, res.affiliations(0)))
			  } else if (res.affiliations.size == res.authors.size) {
		      authors = (res.authors, res.affiliations).zipped.map(Author(_, _))
			  }
			  primaryAuthor = Author(res.authors.last, res.affiliations(0))    
		  } else {
		    authors = res.authors.map(x => Author(x, "Institute not found")) 
			  primaryAuthor = Author(res.authors.last, "Institute not found")
	    }
		  Display(primaryAuthor, authors, res.title)
		}
		result
	}
}