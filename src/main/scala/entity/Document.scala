package es.uvigo.esei.tfg.smartdrugsearch.entity

final case class DocumentId (value : Long) extends AnyVal with Identifier
           object DocumentId extends IdentifierCompanion[DocumentId]

final case class PubMedId (value : Long) extends AnyVal with Identifier
           object PubMedId extends IdentifierCompanion[PubMedId]

case class Document (
  id        : Option[DocumentId] = None,
  title     : Sentence,
  text      : String,
  annotated : Boolean            = false,
  pubmedId  : Option[PubMedId]   = None
) {

  require(!text.isEmpty, "Document text cannot be empty")

}

