package es.uvigo.esei.tfg.smartdrugsearch.controller

import play.api.libs.json.{ Json, JsValue }
import play.api.mvc._

import es.uvigo.esei.tfg.smartdrugsearch.database.DatabaseProfile
import es.uvigo.esei.tfg.smartdrugsearch.entity._

private[controller] trait AnnotationsController extends Controller {

  lazy val database = DatabaseProfile()

  import database._
  import database.profile.simple._

  def get(id : AnnotationId) : Action[AnyContent] =
    Action {
      withAnnotation(id) { annotation => Ok(Json toJson annotation) }
    }

  private[this] def withAnnotation(id : AnnotationId)(f : Annotation => SimpleResult) =
    database withSession { implicit session =>
      (Annotations findById id).firstOption map f getOrElse NotFound(Json obj ("err" -> "Annotation not found"))
    }

}

object AnnotationsController extends AnnotationsController

