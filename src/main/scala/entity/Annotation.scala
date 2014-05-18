package es.uvigo.esei.tfg.smartdrugsearch.entity

final case class AnnotationId (value : Long) extends AnyVal with Identifier
object AnnotationId extends IdentifierCompanion[AnnotationId]

final case class Annotation (
  id            : Option[AnnotationId] = None,
  documentId    : DocumentId,
  keywordId     : KeywordId,
  text          : Sentence,
  startPosition : Position,
  endPosition   : Position
) {

  require(startPosition < endPosition, "Start Position must be inferior than End Position")

}

object Annotation extends ((Option[AnnotationId], DocumentId, KeywordId, Sentence, Position, Position) => Annotation) {

  import play.api.libs.json._
  import play.api.libs.functional.syntax._

  implicit val annotationWrites : Writes[Annotation] = (
    (__ \ 'id).writeNullable[AnnotationId] and
    (__ \ 'documentId).write[DocumentId]   and
    (__ \ 'keywordId).write[KeywordId]     and
    (__ \ 'text).write[Sentence]           and
    (__ \ 'startPosition).write[Position]  and
    (__ \ 'endPosition).write[Position]
  ) (unlift(unapply))

  implicit val annotationReads : Reads[Annotation] = (
    (__ \ 'id).readNullable[AnnotationId] and
    (__ \ 'documentId).read[DocumentId]   and
    (__ \ 'keywordId).read[KeywordId]     and
    (__ \ 'text).read[Sentence]           and
    (__ \ 'startPosition).read[Position]  and
    (__ \ 'endPosition).read[Position]
  ) (apply _)

}
