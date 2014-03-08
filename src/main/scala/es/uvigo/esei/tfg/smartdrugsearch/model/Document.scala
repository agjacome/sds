package es.uvigo.esei.tfg.smartdrugsearch.model

final case class DocumentId (value : Long) extends Identifier
final object     DocumentId extends IdentifierCompanion[DocumentId]

case class Document (
  val id    : Option[DocumentId],
  val title : Sentence,
  val text  : String
)

