package es.uvigo.esei.tfg.smartdrugsearch.controller

import scala.concurrent.duration._

import play.api.cache.Cached
import play.api.libs.json.Json
import play.api.mvc._

import es.uvigo.esei.tfg.smartdrugsearch.entity._
import es.uvigo.esei.tfg.smartdrugsearch.searcher.Searcher

private[controller] trait SearcherController extends Controller {

  import play.api.Play.{ current => app }
  import play.api.libs.concurrent.Execution.Implicits.defaultContext

  lazy val searcher  = Searcher()

  lazy val cacheTime = app.configuration getMilliseconds "searcher.cacheTime" map (
    _.milliseconds.toSeconds.toInt
  ) getOrElse 1800

  def search(searchTerms : Sentence, pageNumber : Position, pageSize : Size) : Cached =
    Cached(_ => s"search($searchTerms, $pageNumber, $pageSize)", cacheTime) {
      Action.async(searcher search (searchTerms, pageNumber, pageSize) map {
        result => Ok(Json toJson result)
      })
    }

}

object SearcherController extends SearcherController

