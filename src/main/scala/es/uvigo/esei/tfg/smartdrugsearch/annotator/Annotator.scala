package es.uvigo.esei.tfg.smartdrugsearch.annotator

import scala.collection.concurrent.TrieMap
import scala.concurrent.duration._
import akka.actor._

import play.api.{ Configuration, Logger }
import play.api.Play.{ current => app }

import es.uvigo.esei.tfg.smartdrugsearch.entity._
import es.uvigo.esei.tfg.smartdrugsearch.database.DatabaseProfile

private[annotator] trait AnnotatorBase extends Actor {

  lazy val database = DatabaseProfile()

  import database._
  import database.profile.simple._

  override final def receive : Receive = {
    case msg : Annotate => annotate(sender, msg)
    case msg : Finished => finished(sender, msg)
    case msg : Failed   => failed(sender, msg)
  }

  protected def annotate(sender : ActorRef, msg : Annotate) : Unit =
    database withSession { implicit session =>
      // TODO: this is an ugly hack to block simultaneous requests to annotate
      // this document; solution: add a "blocked" flag to the document table
      Documents filter (_.id is msg.documentId) map (_.annotated) update true
    }

  protected def finished(sender : ActorRef, msg : Finished) : Unit

  protected def failed(sender : ActorRef, msg : Failed) : Unit

  protected def markAnnotated(documentId : DocumentId) =
    database withSession { implicit session =>
      Documents filter (_.id is documentId) map (_.annotated) update true
    }

  protected def deleteAnnotations(documentId : DocumentId) =
    database withTransaction { implicit session =>
      decrementKeywordCounter(documentId)
      (Annotations findByDocumentId documentId).delete
      Documents filter (_.id is documentId) map (_.annotated) update false
    }

  private[this] def decrementKeywordCounter(documentId : DocumentId)(implicit session : Session) =
    findDocumentKeywords(documentId) foreach {
      case (id, (current, count)) => Keywords filter (_.id is id) map (_.occurrences) update (current - count)
    }

  private[this] def findDocumentKeywords(documentId : DocumentId)(implicit session : Session) =
    (Annotations filter (_.documentId is documentId) flatMap {
      a => Keywords filter (_.id is a.keywordId)
    } groupBy identity map { case (k, ks) => k.id -> (k.occurrences, ks.length) }).list

  protected final def isAnnotated(documentId  : DocumentId) =
    database withSession { implicit session =>
      (Documents filter (_.id is documentId) map (_.annotated)).first
    }

}

private[annotator] trait AnnotatorLogging extends AnnotatorBase {

  abstract override protected def annotate(sender : ActorRef, msg : Annotate) =
    if   (!isAnnotated(msg.documentId)) super.annotate(sender,msg)
    else Logger.warn(s"[${self.path.name}] Ignoring already annotated ${msg.documentId}")

  abstract override protected def finished(sender : ActorRef, msg : Finished) = {
    Logger.info(s"[${sender.path.name}] Finished for ${msg.documentId}")
    super.finished(sender, msg)
  }

  abstract override protected def failed(sender : ActorRef, msg : Failed) = {
    Logger.error(s"[${sender.path.name}] Failed for ${msg.documentId}\n${msg.cause}")
    super.failed(sender, msg)
  }

  abstract override protected def deleteAnnotations(documentId : DocumentId) = {
    Logger.error(s"[${self.path.name}] Some annotator failed, restoring DB for $documentId")
    super.deleteAnnotations(documentId)
  }

  abstract override protected def markAnnotated(documentId : DocumentId) = {
    Logger.info(s"[${self.path.name}] All annotators finished for $documentId")
    super.markAnnotated(documentId)
  }

}

private[annotator] class AnnotatorImpl extends AnnotatorBase {

  // map of (DocumentId -> (CompletedAnnotatorsCounter, HasAnyAnnotatorFailed?))
  private[this] lazy val completed = TrieMap[DocumentId, (Size, Boolean)]()

  private[this] lazy val annotators = createAnnotators(app.configuration getConfig "annotator")

  override protected def annotate(sender : ActorRef, msg : Annotate) =
    if (!completed.contains(msg.documentId)) {
      completed += (msg.documentId -> (0, false))
      annotators foreach (_ ! msg)
    }

  override protected def finished(sender : ActorRef, msg : Finished) =
    if (completed contains msg.documentId) {
      if (hasCompleted(msg.documentId)) wrapUp(msg.documentId) else {
        val (counter, failed) = completed(msg.documentId)
        completed update (msg.documentId, (counter + 1, failed))
      }
    }

  override protected def failed(sender : ActorRef, msg : Failed) =
    if (completed contains msg.documentId) {
      if (hasCompleted(msg.documentId)) wrapUp(msg.documentId) else {
        val (counter, _) = completed(msg.documentId)
        completed update (msg.documentId, (counter + 1, true))
      }
    }

  private[this] def wrapUp(documentId : DocumentId) = {
    if (hasFailed(documentId)) deleteAnnotations(documentId)
    else markAnnotated(documentId)
    completed -= documentId
  }

  private[this] def hasCompleted(documentId : DocumentId) =
    completed(documentId)._1.value == annotators.size - 1

  private[this] def hasFailed(documentId : DocumentId) =
    completed(documentId)._2

  private[this] def createAnnotators(subConfig : Option[Configuration]) =
    subConfig.fold(Set.empty[ActorRef]) {
      config => config.keys map {
        key => context actorOf (Props(Class forName (config getString key).get), key)
      }
    }

}

class Annotator extends AnnotatorImpl with AnnotatorLogging

