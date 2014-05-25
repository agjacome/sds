package es.uvigo.esei.tfg.smartdrugsearch.controller

import play.api.libs.json.{ Json, JsValue }
import play.api.mvc._

import es.uvigo.esei.tfg.smartdrugsearch.database.DatabaseProfile
import es.uvigo.esei.tfg.smartdrugsearch.entity._

private[controller] trait AnnotationsController extends Controller {

  lazy val database = DatabaseProfile()

  import database._
  import database.profile.simple._

  def list(pageNumber : Option[Position], pageSize : Option[Size]) : Action[AnyContent] =
    Action {
      listResult(pageNumber getOrElse 1, pageSize getOrElse 50)
    }

  def get(id : AnnotationId) : Action[AnyContent] =
    Action {
      withAnnotation(id) { annotation => Ok(Json toJson annotation) }
    }

  private[this] def listResult(pageNumber : Position, pageSize : Size) =
    database withSession { implicit session =>
      val total  = Size(Keywords.count)
      val toTake = pageSize.toInt
      val toDrop = (pageNumber.toInt - 1) * toTake
      val list   = (Annotations drop toDrop take toTake).list
      Ok(Json toJson AnnotationList(total, pageNumber, pageSize, list))
    }

  private[this] def withAnnotation(id : AnnotationId)(f : Annotation => SimpleResult) =
    database withSession { implicit session =>
      (Annotations findById id).firstOption map f getOrElse NotFound(Json obj ("err" -> "Annotation not found"))
    }

}

object AnnotationsController extends AnnotationsController

