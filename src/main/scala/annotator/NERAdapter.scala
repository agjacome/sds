package es.uvigo.esei.tfg.smartdrugsearch.annotator

import scala.concurrent.Future

import akka.actor.Actor
import akka.pattern.pipe

import es.uvigo.esei.tfg.smartdrugsearch.entity.Document
import es.uvigo.esei.tfg.smartdrugsearch.database.DatabaseProfile
import es.uvigo.esei.tfg.smartdrugsearch.database.dao.DocumentsDAO

private[annotator] sealed trait NERMessage
private[annotator] case class Annotate (document : Document) extends NERMessage
private[annotator] case class Finished (document : Document) extends NERMessage
private[annotator] case class Failed   (document : Document, cause : Throwable) extends NERMessage

private[annotator] trait NERAdapter extends Actor {

  val db : DatabaseProfile = DatabaseProfile()

  import context._
  import db.profile.simple.Session

  protected implicit var session : Session = _

  override final def receive = configurationState

  private def configurationState : Receive = {
    case session : Session =>
      this.session = session
      become(annotationState)
  }

  private def annotationState : Receive = {
    case Annotate(document) =>
      try {
        (check _ andThen annotate)(document) pipeTo sender()
      } catch {
        case e : Exception => sender() ! Failed(document, e)
      }
  }

  private def check(document : Document) : Document = {
    require(DocumentsDAO() contains document , "Document must be stored in Database")
    document
  }

  protected def annotate(document : Document) : Future[Finished]

}

