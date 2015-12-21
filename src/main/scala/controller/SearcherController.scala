package es.uvigo.ei.sing.sds
package controller

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.mvc._

import entity._
import searcher._

object SearcherController extends Controller {

  lazy val searcher = new Searcher

  implicit val SearchResultWrites: Writes[(Article, Double, Set[Keyword])] = (
    (__ \ 'article).write[Article] and
    (__ \ 'tfidf).write[Double] and
    (__ \ 'keywords).write[Set[Keyword]]
  )(s => s)

  def search(query: String, page: Option[Int], pageSize: Option[Int]): Action[AnyContent] =
    Action.async(searcher.search(query, page.getOrElse(0), pageSize.getOrElse(50)) map {
      result => Ok(Json.toJson(result))
    })

  def advSearch(query: String, page: Option[Int], pageSize: Option[Int], categories: List[Category], fromYear: Long, toYear: Long): Action[AnyContent] =
    Action.async(searcher.advSearch(query, page.getOrElse(0), pageSize.getOrElse(50), categories.toSet, fromYear, toYear) map {
      result => Ok(Json.toJson(result))
    })

}

