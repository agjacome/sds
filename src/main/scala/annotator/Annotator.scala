package es.uvigo.esei.tfg.smartdrugsearch.annotator

import scala.collection.concurrent.TrieMap
import akka.actor._

import play.api.Logger

import es.uvigo.esei.tfg.smartdrugsearch.entity.Document
import es.uvigo.esei.tfg.smartdrugsearch.database.dao._

class Annotator extends Actor {

  import play.api.Play.{ current => app }
  import es.uvigo.esei.tfg.smartdrugsearch.database.DatabaseProfile.database

  private[this] val annotators = app.configuration getConfig "annotator" match {
    case None         => Set.empty
    case Some(config) => config.keys map {
      key => context actorOf (Props(Class forName config.getString(key).get), key)
    }
  }

  // map of (Document -> (CompletedAnnotatorsCounter, HasAnyAnnotatorFailed?))
  private[this] lazy val completed = TrieMap[Document, (Int, Boolean)]()

  private[this] lazy val annotations = AnnotationsDAO()
  private[this] lazy val documents   = DocumentsDAO()
  private[this] lazy val keywords    = KeywordsDAO()


  override final def receive : Receive = {
    case document : Document     => annotate(document)
    case Finished(document)      => finished(sender, document)
    case Failed(document, cause) => failed(sender, document, cause)
  }


  private[this] def annotate(document : Document) =
    if (!document.annotated) {
      begin(document)
      annotators foreach { _ ! Annotate(document) }
    } else Logger.warn(s"[${self.path.name}] Ignoring already annotated: ${document.title}")

  private[this] def finished(sender : ActorRef, document : Document) = {
    Logger.info(s"[${sender.path.name}] Finished: ${document.title}")
    if (hasCompleted(document)) wrapUp(document) else complete(document)
  }

  private[this] def failed(sender : ActorRef, document : Document, cause : Throwable) = {
    Logger.error(s"[${sender.path.name}] Failed: ${document.title}\n${cause}")
    if (hasCompleted(document)) wrapUp(document) else complete(document, true)
  }


  private[this] def begin(document : Document) =
    completed += (document -> (0, false))

  private[this] def complete(document : Document, failed : Boolean = false) =
    completed update (document, (completed(document)._1 + 1, failed))

  private[this] def terminate(document : Document) =
    completed -= document

  private[this] def hasCompleted(document : Document) =
    completed(document)._1 == annotators.size - 1

  private[this] def hasFailed(document : Document) =
    completed(document)._2

  private[this] def wrapUp(document : Document) = {
    if (hasFailed(document)) {
      Logger.error(s"[${self.path.name}] Some annotator failed, restoring DB state: ${document.title}")
      deleteAnnotations(document)
    } else {
      Logger.info(s"[${self.path.name}] All annotators finished: ${document.title}")
      markAsAnnotated(document)
    }
    terminate(document)
  }


  private[this] def markAsAnnotated(document : Document) =
    database withSession { implicit session =>
      documents save document.copy(annotated = true)
    }

  private[this] def deleteAnnotations(document : Document) =
    database withTransaction { implicit session =>
      annotations findByDocument document foreach { annotation =>
        annotations keywordFor annotation map {
          keyword => keywords save keyword.copy(occurrences = keyword.occurrences - 1)
        }
        annotations delete annotation
      }
    }

}

