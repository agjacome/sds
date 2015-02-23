package es.uvigo.ei.sing.sds.annotator

import scala.collection.concurrent.TrieMap
import akka.actor._

import play.api.{ Configuration, Logger }
import play.api.Play.{ current => app }

import es.uvigo.ei.sing.sds.entity._
import es.uvigo.ei.sing.sds.database.DatabaseProfile

private[annotator] trait AnnotatorBase extends Actor {

  lazy val database = DatabaseProfile()

  import database._
  import database.profile.simple._

  override final def receive : Receive = {
    case msg : Annotate => annotate(sender, msg.documentId)
    case msg : Finished => finished(sender, msg.documentId)
    case msg : Failed   => failed(sender, msg.documentId, msg.cause)
  }

  protected def annotate(sender : ActorRef, documentId : DocumentId) : Unit

  protected def finished(sender : ActorRef, documentId : DocumentId) : Unit

  protected def failed(sender : ActorRef, documentId : DocumentId, cause : Throwable) : Unit

  protected final def isAnnotated(documentId  : DocumentId) =
    database withSession { implicit session =>
      (Documents filter (_.id is documentId) map (_.annotated)).first
    }

  protected final def isBlocked(documentId : DocumentId) =
    database withSession { implicit session =>
      (Documents filter (_.id is documentId) map (_.blocked)).first
    }

  protected def markAnnotated(documentId : DocumentId, annotated : Boolean) =
    database withSession { implicit session =>
      Documents filter (_.id is documentId) map (_.annotated) update annotated
    }

  protected def markBlocked(documentId : DocumentId, blocked : Boolean) =
    database withSession { implicit session =>
      Documents filter (_.id is documentId) map (_.blocked) update blocked
    }

  protected def deleteAnnotations(documentId : DocumentId) =
    database withTransaction { implicit session =>
      decrementKeywordCounter(documentId)
      (Annotations findByDocumentId documentId).delete
      markAnnotated(documentId, false)
      markBlocked(documentId, false)
    }

  private[this] def decrementKeywordCounter(documentId : DocumentId)(implicit session : Session) =
    findDocumentKeywords(documentId) foreach {
      case (id, (current, count)) => Keywords filter (_.id is id) map (_.occurrences) update (current - count)
    }

  private[this] def findDocumentKeywords(documentId : DocumentId)(implicit session : Session) =
    (Annotations filter (_.documentId is documentId) flatMap {
      a => Keywords filter (_.id is a.keywordId)
    } groupBy identity map { case (k, ks) => (k.id, (k.occurrences, ks.length)) }).list

}

private[annotator] trait AnnotatorLogging extends AnnotatorBase {

  abstract override protected def annotate(sender : ActorRef, documentId : DocumentId) =
    if (isAnnotated(documentId))
      Logger.warn(s"[${self.path.name}] Ignorning already annotated ${documentId}")
    else if (isBlocked(documentId))
      Logger.warn(s"[${self.path.name}] Ignornig blocked (currently annotating) ${documentId}")
    else super.annotate(sender, documentId);

  abstract override protected def finished(sender : ActorRef, documentId : DocumentId) = {
    Logger.info(s"[${sender.path.name}] Finished for ${documentId}")
    super.finished(sender, documentId)
  }

  abstract override protected def failed(sender : ActorRef, documentId : DocumentId, cause : Throwable) = {
    Logger.error(s"[${sender.path.name}] Failed for ${documentId}\n${cause}")
    super.failed(sender, documentId, cause)
  }

  abstract override protected def deleteAnnotations(documentId : DocumentId) = {
    Logger.error(s"[${self.path.name}] Some annotator failed, restoring DB for $documentId")
    super.deleteAnnotations(documentId)
  }

  abstract override protected def markAnnotated(documentId : DocumentId, annotated : Boolean) = {
    if (annotated) Logger.info(s"[${self.path.name}] All annotators finished for $documentId")
    super.markAnnotated(documentId, annotated)
  }

}

private[annotator] class AnnotatorImpl extends AnnotatorBase {

  // map of (DocumentId -> (CompletedAnnotatorsCounter, HasAnyAnnotatorFailed?))
  private[this] lazy val completed = TrieMap[DocumentId, (Size, Boolean)]()

  private[this] lazy val annotators = createAnnotators(app.configuration getConfig "annotator")

  override protected def annotate(sender : ActorRef, documentId : DocumentId) = {
    markBlocked(documentId, true)
    completed += ((documentId, (0, false)))
    annotators foreach (_ ! Annotate(documentId))
  }

  override protected def finished(sender : ActorRef, documentId : DocumentId) : Unit =
    if (hasCompleted(documentId))
      wrapUp(documentId)
    else {
      val (counter, failed) = completed(documentId)
      completed update(documentId, (counter + 1, failed))
    }

  override protected def failed(sender : ActorRef, documentId : DocumentId, cause : Throwable) =
    if (hasCompleted(documentId)) wrapUp(documentId) else {
      val (counter, _) = completed(documentId)
      completed update (documentId, (counter + 1, true))
    }

  private[this] def wrapUp(documentId : DocumentId) : Unit = {
    if (hasFailed(documentId)) deleteAnnotations(documentId) else {
      markAnnotated(documentId, true)
      markBlocked(documentId, false)
    }
    completed -= documentId
    ()
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

