package es.uvigo.esei.tfg.smartdrugsearch.entity

final case class DocumentId (value : Long) extends AnyVal with Identifier
object DocumentId extends IdentifierCompanion[DocumentId]

final case class Document (
  id        : Option[DocumentId] = None,
  title     : Sentence,
  text      : String,
  annotated : Boolean            = false,
  pubmedId  : Option[PubMedId]   = None
) {

  require(!text.isEmpty, "Document text cannot be empty")

}

object Document extends ((Option[DocumentId], Sentence, String, Boolean, Option[PubMedId]) => Document) {

  import play.api.data.Form
  import play.api.data.Forms._
  import play.api.libs.json._
  import play.api.libs.functional.syntax._

  private def formApply(title : String, text : String, pubmedId : Option[Long]) : Document =
    apply(None, title, text, false, pubmedId map PubMedId)

  private def formUnapply(document : Document) : Option[(String, String, Option[Long])] =
    Some((document.title, document.text, document.pubmedId map (_.toLong)))

  lazy val form = Form(mapping(
    "title"    -> nonEmptyText,
    "text"     -> nonEmptyText,
    "pubmedId" -> optional(longNumber(min = 1L))
  )(formApply)(formUnapply))

  implicit val documentWrites = Json.writes[Document]

  // make annotated an optional field in JSON object, defaulting it to false
  implicit val documentReads = (
    (__ \ 'id).readNullable[DocumentId]     and
    (__ \ 'title).read[Sentence]            and
    (__ \ 'text).read[String]               and
    (__ \ 'annotated).readNullable[Boolean].map(_ getOrElse false) and
    (__ \ 'pubmedId).readNullable[PubMedId]
  ) (apply _)

}

