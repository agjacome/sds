package es.uvigo.esei.tfg.smartdrugsearch.model.annotator

import scala.concurrent.Future

import akka.actor.Actor
import akka.pattern.pipe

import es.uvigo.esei.tfg.smartdrugsearch.model.Document
import es.uvigo.esei.tfg.smartdrugsearch.model.database.DAL

private[annotator] final case class AnnotateDocument   (document : Document)
private[annotator] final case class FinishedAnnotation (document : Document)
private[annotator] final case class FailedAnnotation   (document : Document, cause : Throwable)

private[annotator] trait NERAdapter extends Actor {

  protected val dal : DAL

  import context._
  import dal._
  import dal.profile.simple._

  protected implicit var session : Session = _

  override final def receive : Receive = configurationState

  private def configurationState : Receive = {
    case session : Session =>
      this.session = session
      become(annotationState)
  }

  private def annotationState : Receive = {
    case AnnotateDocument(document) =>
      try {
        (check _ andThen annotate)(document) pipeTo sender()
      } catch {
        case e : Exception => sender() ! FailedAnnotation(document, e)
      }
  }

  private def check(document : Document) : Document = {
    require(document.id.isDefined       , "Document must have a defined Id")
    require(Documents contains document , "Document must be stored in Database")
    document
  }

  protected def annotate(document : Document) : Future[FinishedAnnotation]

}

