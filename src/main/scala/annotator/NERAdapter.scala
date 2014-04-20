package es.uvigo.esei.tfg.smartdrugsearch.annotator

import scala.concurrent.Future

import akka.actor.Actor
import akka.pattern.pipe

import es.uvigo.esei.tfg.smartdrugsearch.entity.Document
import es.uvigo.esei.tfg.smartdrugsearch.database.DatabaseProfile
import es.uvigo.esei.tfg.smartdrugsearch.database.dao._

private[annotator] sealed trait NERMessage
private[annotator] case class Annotate (document : Document)  extends NERMessage
private[annotator] case class Finished (document : Document)  extends NERMessage
private[annotator] case class Failed   (document : Document, cause : Throwable) extends NERMessage

private[annotator] trait NERAdapter extends Actor {

  import context._

  protected lazy val dbProfile   = DatabaseProfile()
  protected lazy val Documents   = DocumentsDAO()
  protected lazy val Keywords    = KeywordsDAO()
  protected lazy val Annotations = AnnotationsDAO()

  override final def receive : Receive = {
    case Annotate(document) =>
      try {
        checkDocument(document)
        annotate(document) pipeTo sender
    } catch {
      case e : Exception => sender ! Failed(document, e)
    }
  }

  private def checkDocument(document : Document) : Unit =
    dbProfile.database withSession { implicit session =>
      require(Documents contains document, "Documents must already be stored in Database")
    }

  protected def annotate(document : Document) : Future[Finished]

}

