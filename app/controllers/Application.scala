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
import play.api.data._
import play.api.data.Forms._


case class SearchForm(firstName: String, lastName: Option[String], limit: Option[Int])


object Application extends Controller {
  val searchForm = Form(
      mapping(
          "firstName" -> nonEmptyText,
          "lastName"  -> optional(text),
          "limit"     -> optional(number)
      )(SearchForm.apply)(SearchForm.unapply)
  )
  
	def index = Action {
		Ok(views.html.request_form(searchForm))
	}

	def search(term: String, limit: Int) = Action {
		try {
		  val formattedStr = term.replaceAll("\\s+", "+")
		  val res = performSearch(formattedStr, limit)
      Ok(views.html.results(res))
		} catch {
			case e => InternalServerError("Something bad hapened " + e)
		}
	}

  private def sort(col: Int, order: Int, in: List[Display]) : List[Display] = {
     
     val out = order match {
       //ascending
       case 1 => {
         col match {
           case 0 => {
             in.sortWith(_.title.toLowerCase < _.title.toLowerCase)
           }
           case 1 => {
             in.sortWith(_.journal.toLowerCase < _.journal.toLowerCase)
           }
           case 2 => {
             in.sortWith(_.primaryAuthor.name.toLowerCase < _.primaryAuthor.name.toLowerCase)        
           }
           case _ => {
             in.sortWith(_.primaryAuthor.institute.toLowerCase < _.primaryAuthor.institute.toLowerCase)        
           }
         }
       }
       //descending
       case _ => {
         col match {
           case 0 => {
             in.sortWith(_.title.toLowerCase < _.title.toLowerCase)
           }
           case 1 => {
             in.sortWith(_.journal.toLowerCase < _.journal.toLowerCase)
           }
           case 2 => {
             in.sortWith(_.primaryAuthor.name.toLowerCase < _.primaryAuthor.name.toLowerCase)        
           }
           case _ => {
             in.sortWith(_.primaryAuthor.institute.toLowerCase < _.primaryAuthor.institute.toLowerCase)        
           }
         }
       }
     }
     out
  }
  
  
  
	def performSearch(searchStr: String, limit: Int) : List[Display] = {
	  val pmids = Pubmed.search(searchStr).esearchresult.idlist
	  if (limit>0) {
	    queryTogows(pmids.take(limit))
	  } else {
	    queryTogows(pmids)	  	
	  }
	}
	
  // Handle the submitted form and perform subsequent actions	
  def submit = Action { implicit request =>
    searchForm.bindFromRequest.fold(
        formWithErrors => {
          BadRequest(views.html.request_form(formWithErrors))
        },
        form => {
          val name = form.lastName match {
            case Some(last) => form.firstName + "+" + last
            case None => form.firstName
          }
          val numOfResults = form.limit match {
            case Some(num) => num
            case None => 0
          }
          Redirect(routes.Application.search(name, numOfResults))
        }
     )
	}
  
  private def queryTogows(pmids: Seq[String]) : List[Display] = {
    val result = for {
      res <- Togows.getDetails(pmids)
    } yield {
      var authors : Seq[Author] = Nil
      var primaryAuthor : Author = null
      res.authors.size match {
        case 1 => {
          res.affiliations.size match {
            case 1 => primaryAuthor = Author(res.authors.last, res.affiliations.last)
            case _ => primaryAuthor = Author(res.authors.last, "Institute not found")
          }
        }
        case x if (res.authors.size > 1) => {
          res.affiliations.size match {
            case 1 => {
              primaryAuthor = Author(res.authors.last, res.affiliations.last)
            }
            case x if (x == res.authors.size) => {
              authors = (res.authors.take(res.authors.size-1), res.affiliations.take(res.authors.size-1)).zipped.map(Author(_, _))
              primaryAuthor = Author(res.authors.last, res.affiliations.last) 
            }
            case _ => {
              authors = res.authors.map(x => Author(x, "Institute not found")) 
              primaryAuthor = Author(res.authors.last, "Institute not found")
            }
          }
        }
      }
      Display(primaryAuthor, Option(authors), res.title, res.journal)
    }
    result
  }
}