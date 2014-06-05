package es.uvigo.esei.tfg.smartdrugsearch.controller

import scala.concurrent.duration._

import play.api.cache.Cached
import play.api.libs.json.{ Json, JsValue }
import play.api.mvc._

import es.uvigo.esei.tfg.smartdrugsearch.database.DatabaseProfile
import es.uvigo.esei.tfg.smartdrugsearch.entity._

private[controller] trait DocumentsController extends Controller with Authorization {

  lazy val database = DatabaseProfile()

  import database._
  import database.profile.simple._

  lazy val cacheTime = app.configuration getMilliseconds "application.cacheTime" map (
    _.milliseconds.toSeconds.toInt
  ) getOrElse 300

  def list(pageNumber : Option[Position], pageSize : Option[Size]) : Action[AnyContent] =
    Action(listResult(pageNumber getOrElse 1, pageSize getOrElse 50))

  def get(id : DocumentId) : Cached =
    Cached(_ => s"documentGet($id)", cacheTime) {
      Action(withAnnotatedDocument(id) { document => Ok(Json toJson document) })
    }

  def add : Action[JsValue] =
    AuthorizedAction(parse.json) { _ => request =>
      Document.form bind request.body fold (
        errors => BadRequest(Json obj ("err" -> errors.errorsAsJson)),
        addResult
      )
    }

  def delete(id : DocumentId) : Action[AnyContent] =
    AuthorizedAction() { _ => _ =>
      withDocument(id)(deleteResult)
    }

  private[this] def listResult(pageNumber : Position, pageSize : Size) =
    database withSession { implicit session =>
      val total  = Size(Documents.count)
      val toTake = pageSize.toInt
      val toDrop = (pageNumber.toInt - 1) * toTake
      val list   = (Documents sortBy(_.id) drop toDrop take toTake).list
      Ok(Json toJson DocumentList(total, pageNumber, pageSize, list))
    }

  private[this] def addResult(document : Document) =
    database withSession { implicit session =>
      Created(Json obj ("id" -> (Documents += document)))
    }

  private[this] def deleteResult(document : Document) =
    database withSession { implicit session =>
      if (document.blocked)
        Forbidden(Json obj ("err" -> "Cannot delete a document with an ongoing annotation process."))
      else {
        Documents -= document
        NoContent
      }
    }

  private[this] def withDocument(id : DocumentId)(f : Document => SimpleResult) =
    database withSession { implicit session =>
      (Documents findById id).firstOption map f getOrElse NotFound(Json obj ("err" -> "Document not found"))
    }

  private[this] def withAnnotatedDocument(id : DocumentId)(f : AnnotatedDocument => SimpleResult) =
    withDocument(id)(document => database withSession { implicit session =>
      val as = Annotations filter (_.documentId is id)
      val ks = as join Keywords on (_.keywordId is _.id) map (_._2) groupBy identity map (_._1)
      f(AnnotatedDocument(document, as.list.toSet, ks.list.toSet))
    })

}

object DocumentsController extends DocumentsController

