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

object Document extends ((Option[DocumentId], Sentence, String, Boolean, Option[PubMedId]) => Document) {

  import play.api.libs.json._
  import play.api.libs.functional.syntax._

  implicit val documentWrites : Writes[Document] = (
    (__ \ 'id).writeNullable[DocumentId] and
    (__ \ 'title).write[Sentence]        and
    (__ \ 'text).write[String]           and
    (__ \ 'annotated).write[Boolean]     and
    (__ \ 'pubmedId).writeNullable[PubMedId]
  ) (unlift(unapply))

  implicit val documentReads : Reads[Document] = (
    (__ \ 'id).readNullable[DocumentId]     and
    (__ \ 'title).read[Sentence]            and
    (__ \ 'text).read[String]               and
    (__ \ 'annotated).readNullable[Boolean].map(_ getOrElse false) and
    (__ \ 'pubmedId).readNullable[PubMedId]
  ) (apply _)

}

