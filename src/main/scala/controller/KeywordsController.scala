package es.uvigo.esei.tfg.smartdrugsearch.controller

import play.api.libs.json.{ Json, JsValue }
import play.api.mvc._

import es.uvigo.esei.tfg.smartdrugsearch.database.DatabaseProfile
import es.uvigo.esei.tfg.smartdrugsearch.entity._

private[controller] trait KeywordsController extends Controller {

  lazy val database = DatabaseProfile()

  import database._
  import database.profile.simple._

  def list(pageNumber : Option[Position], pageSize : Option[Size]) : Action[AnyContent] =
    Action {
      listResult(pageNumber getOrElse 1, pageSize getOrElse 50)
    }

  def get(id : KeywordId) : Action[AnyContent] =
    Action {
      withKeyword(id) { keyword => Ok(Json toJson keyword) }
    }

  private[this] def listResult(pageNumber : Position, pageSize : Size) =
    database withSession { implicit session =>
      val total  = Size(Keywords.count)
      val toTake = pageSize.toInt
      val toDrop = (pageNumber.toInt - 1) * toTake
      val list   = (Keywords drop toDrop take toTake).list
      Ok(Json toJson KeywordList(total, pageNumber, pageSize, list))
    }

  private[this] def withKeyword(id : KeywordId)(f : Keyword => SimpleResult) =
    database withSession { implicit session =>
      (Keywords findById id).firstOption map f getOrElse NotFound(Json obj ("err" -> "Keyword not found"))
    }

}

object KeywordsController extends KeywordsController

