package es.uvigo.esei.tfg.smartdrugsearch.annotator

import scala.collection.concurrent.TrieMap
import akka.actor._

import play.api.Logger
import play.api.Configuration

import es.uvigo.esei.tfg.smartdrugsearch.entity.Document
import es.uvigo.esei.tfg.smartdrugsearch.database.DatabaseProfile
import es.uvigo.esei.tfg.smartdrugsearch.database.dao._

class Annotator extends Actor {

  import play.api.Play.{ current => app }

  // map of (Document -> (FinishedAnnotatorsCounter, HasAnyAnnotatorFailed?))
  private[this] lazy val finished = TrieMap[Document, (Int, Boolean)]()

  private lazy val dbProfile   = DatabaseProfile()
  private lazy val Annotations = AnnotationsDAO()
  private lazy val Documents   = DocumentsDAO()
  private lazy val Keywords    = KeywordsDAO()

  private lazy val annotators : Set[ActorRef] =
    app.configuration getConfig "annotator" match {
      case Some(cfg) => cfg.keys flatMap { createAnnotator(cfg, _) }
      case None      => Set.empty
    }

  private def createAnnotator(config : Configuration, key : String) : Option[ActorRef] =
    config getString key map {
      clazz => context actorOf (Props(Class forName clazz), key)
    }


  override final def receive : Receive = {
    case document : Document     => annotate(document)
    case Finished(document)      => finished(sender, document)
    case Failed(document, cause) => failed(sender, document, cause)
  }


  private[this] def annotate(document : Document) : Unit =
    if (document.annotated) Logger.warn(s"[${self.path.name}] Ignoring already annotated: ${document.title}")
    else {
      begin(document)
      annotators foreach { _ ! Annotate(document) }
    }

  private[this] def finished(sender : ActorRef, document : Document) : Unit = {
    Logger.info(s"[${sender.path.name}] Finished: ${document.title}")
    if (hasCompleted(document)) wrapUp(document) else complete(document)
  }

  private[this] def failed(sender : ActorRef, document : Document, cause : Throwable) : Unit = {
    Logger.error(s"[${sender.path.name}] Failed: ${document.title}\n${cause}")
    if (hasCompleted(document)) wrapUp(document) else complete(document, true)
  }


  private[this] def begin(document : Document) : Unit =
    finished += (document -> (0, false))

  private[this] def complete(document : Document, failed : Boolean = false) : Unit =
    finished update (document, (finished(document)._1 + 1, failed))

  private[this] def hasCompleted(document : Document) : Boolean =
    finished(document)._1 == annotators.size - 1

  private[this] def hasFailed(document : Document) : Boolean =
    finished(document)._2


  private[this] def wrapUp(document : Document) : Unit = {
    if (hasFailed(document)) {
      Logger.error(s"[${self.path.name}] Some annotator failed, restoring DB state: ${document.title}")
      deleteAnnotations(document)
    } else {
      Logger.info(s"[${self.path.name}] All annotators finished: ${document.title}")
      markAsAnnotated(document)
    }
    finished -= document
  }

  private[this] def markAsAnnotated(document : Document) : Unit =
    dbProfile.database withSession { implicit session =>
      Documents save document.copy(annotated = true)
    }

  private[this] def deleteAnnotations(document : Document) : Unit =
    dbProfile.database withTransaction { implicit session =>
      Annotations findByDocument document foreach { annotation =>
        Annotations keywordFor annotation map {
          keyword => Keywords save keyword.copy(occurrences = keyword.occurrences - 1)
        }
        Annotations delete annotation
      }
    }

}

