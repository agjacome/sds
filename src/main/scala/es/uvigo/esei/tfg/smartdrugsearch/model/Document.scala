package es.uvigo.esei.tfg.smartdrugsearch.model

import org.virtuslab.unicorn.ids._

final case class DocumentId (val id : Long) extends AnyVal with BaseId

object DocumentId extends IdCompanion[DocumentId] {

  object Predef {
    import scala.language.implicitConversions

    implicit def longToDocumentId(id : Long) : DocumentId = DocumentId(id)
    implicit def documentIdToLong(id : DocumentId) : Long = id.id
  }

}

case class Document (
  val id    : Option[DocumentId],
  val title : Sentence,
  val text  : String
) extends WithId[DocumentId] {

  require(!text.isEmpty, "Document text must be non-empty")

}

