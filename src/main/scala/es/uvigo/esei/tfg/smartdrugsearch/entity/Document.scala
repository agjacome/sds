package es.uvigo.esei.tfg.smartdrugsearch.entity

import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json._
import play.api.libs.functional.syntax._

import org.jsoup.Jsoup

final case class DocumentId (value : Long) extends AnyVal with Identifier
object DocumentId extends IdentifierCompanion[DocumentId]

final case class Document (
  id        : Option[DocumentId] = None,
  title     : Sentence,
  text      : String,
  pubmedId  : Option[PubMedId] = None,
  annotated : Boolean          = false,
  blocked   : Boolean          = false
) {

  require(!text.isEmpty, "Document text cannot be empty")

}

object Document extends ((Option[DocumentId], Sentence, String, Option[PubMedId], Boolean, Boolean) => Document) {

  lazy val form = Form(mapping(
    "title"    -> nonEmptyText,
    "text"     -> nonEmptyText,
    "pubmedId" -> optional(longNumber(min = 1L))
  )(formApply)(formUnapply))

  implicit val documentWrites = Json.writes[Document]

  private def formApply(title : String, text : String, pubmedId : Option[Long]) : Document =
    apply(None, (Jsoup parse title).text, (Jsoup parse text).text, pubmedId map PubMedId)

  private def formUnapply(document : Document) : Option[(String, String, Option[Long])] =
    Some((document.title, document.text, document.pubmedId map (_.toLong)))

}

final case class AnnotatedDocument (
  document    : Document,
  annotations : Set[Annotation],
  keywords    : Set[Keyword]
)

object AnnotatedDocument extends ((Document, Set[Annotation], Set[Keyword]) => AnnotatedDocument) {

  implicit val annotatedDocumentWrites = Json.writes[AnnotatedDocument]

}

