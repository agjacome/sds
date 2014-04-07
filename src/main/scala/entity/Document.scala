package es.uvigo.esei.tfg.smartdrugsearch.entity

final case class DocumentId (value : Long) extends Identifier
final object     DocumentId extends IdentifierCompanion[DocumentId]

case class Document (
  val id    : Option[DocumentId],
  val title : Sentence,
  val text  : String
) {

  require(!text.isEmpty, "Document text cannot be empty")

}
