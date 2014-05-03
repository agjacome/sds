package es.uvigo.esei.tfg.smartdrugsearch.annotator

import scala.concurrent.Future

import akka.actor.Actor
import akka.pattern.pipe

import es.uvigo.esei.tfg.smartdrugsearch.entity._
import es.uvigo.esei.tfg.smartdrugsearch.database.dao._

private[annotator] sealed trait NERMessage
private[annotator] case class Annotate (document : Document)  extends NERMessage
private[annotator] case class Finished (document : Document)  extends NERMessage
private[annotator] case class Failed   (document : Document, cause : Throwable) extends NERMessage

private[annotator] trait NERAdapter extends Actor {

  import context._
  import es.uvigo.esei.tfg.smartdrugsearch.database.DatabaseProfile.database

  private lazy val documents   = DocumentsDAO()
  private lazy val keywords    = KeywordsDAO()
  private lazy val annotations = AnnotationsDAO()

  override final def receive : Receive = {
    case Annotate(document) =>
      try {
        require(isStored(document), "Documents must already be stored in Database")
        annotate(document) pipeTo sender
      } catch {
        case e: Exception => sender ! Failed(document, e)
      }
  }

  protected def annotate(document : Document) : Future[Finished]

  private def isStored(document : Document) =
    database withSession { implicit session => documents contains document }

  protected def getOrStoreNewKeyword(normalized : Sentence, cat : Category) =
    database withTransaction { implicit session =>
      (keywords findByNormalized normalized) getOrElse {
        val keyword = Keyword(None, normalized, cat)
        keyword copy (id = Some(keywords save keyword))
      }
    }

  protected def storeAnnotation(keyword : Keyword, annotation : Annotation) =
    database withTransaction { implicit session =>
      val current = (keywords findById keyword.id).get
      annotations save annotation
      keywords save (current copy (occurrences = current.occurrences + 1))
    }

}

