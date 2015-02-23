package es.uvigo.esei.tfg.smartdrugsearch.annotator

import scala.concurrent.duration._
import scala.concurrent.Future
import scala.language.postfixOps
import scala.util.{ Failure, Success }

import akka.actor.{ Actor, ActorRef }
import akka.pattern.pipe

import play.api.cache.Cache
import play.api.Play.current

import es.uvigo.esei.tfg.smartdrugsearch.database.DatabaseProfile
import es.uvigo.esei.tfg.smartdrugsearch.entity._

private[annotator] trait AnnotatorAdapter extends Actor {

  lazy val database = DatabaseProfile()

  import context._
  import database._
  import database.profile.simple._

  override final def receive : Receive = {
    case Annotate(documentId) => respondToSender(documentId, sender)
  }

  protected def annotate(document : Document) : Future[Unit]

  private[this] final def respondToSender(documentId : DocumentId, sender : ActorRef) =
    (getDocument _ andThen annotate)(documentId) onComplete {
      case Success(_)     => sender ! Finished(documentId)
      case Failure(cause) => sender ! Failed(documentId, cause)
    }

  private[this] def getDocument(id : DocumentId) =
    Cache.getAs[Document](s"Annotator(${id.toString})") getOrElse {
      val document = database withSession { implicit session => (Documents findById id).first }
      Cache set (s"Annotator(${id.toString})", document, 15 seconds)
      document
    }

  protected final def getOrStoreKeyword(normalized : Sentence, category : Category) =
    database withTransaction { implicit session =>
      (Keywords filter (_.normalized is normalized) map (_.id)).firstOption getOrElse {
        val keyword = Keyword(None, normalized, category)
        Keywords += keyword
      }
    }

  protected def storeAnnotation(annotation : Annotation) =
    database withTransaction { implicit session =>
      val counter = Keywords filter (_.id is annotation.keywordId) map (_.occurrences)
      counter update (counter.first + 1)
      Annotations += annotation
    }

}

