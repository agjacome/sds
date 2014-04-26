package es.uvigo.esei.tfg.smartdrugsearch.entity

final case class DocumentId (value : Long) extends Identifier
final object     DocumentId extends IdentifierCompanion[DocumentId]

final case class PubmedId (value : Long) extends Identifier
final object     PubmedId extends IdentifierCompanion[PubmedId]

case class Document (
  val id        : Option[DocumentId] = None,
  val title     : Sentence,
  val text      : String,
  val annotated : Boolean            = false,
  val pubmedId  : Option[PubmedId]   = None
) {

  require(!text.isEmpty, "Document text cannot be empty")

}
