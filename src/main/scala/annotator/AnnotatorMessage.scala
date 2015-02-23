package es.uvigo.ei.sing.sds.annotator

import es.uvigo.ei.sing.sds.database.DatabaseProfile
import es.uvigo.ei.sing.sds.entity.DocumentId

private[annotator] sealed trait AnnotatorMessage {

  val documentId : DocumentId

}

private[annotator] final case class Finished (documentId : DocumentId) extends AnnotatorMessage
private[annotator] final case class Failed   (documentId : DocumentId, cause : Throwable) extends AnnotatorMessage

// Only check that the document is stored in the Annotator message; we don't
// care about its existence in the Finished and Failed ones, since those will
// only be used internally by the annotator(s) and always after an Annotate
// message for that ID has already been received
final case class Annotate (documentId : DocumentId) extends AnnotatorMessage {

  require(isStored(documentId), "DocumentId does not exist in database.")

  private[this] def isStored(id : DocumentId) = {
    val database = DatabaseProfile()
    import database.profile.simple._

    database withSession { implicit session =>
      (database.Documents filter (_.id is id)).exists.run
    }
  }

}


