package es.uvigo.esei.tfg.smartdrugsearch.entity

final case class AnnotationId (value : Long) extends AnyVal with Identifier
final object     AnnotationId extends IdentifierCompanion[AnnotationId]

case class Annotation (
  val id       : Option[AnnotationId] = None,
  val docId    : DocumentId,
  val keyId    : KeywordId,
  val text     : Sentence,
  val startPos : Position,
  val endPos   : Position
) {

  require(startPos < endPos, "Start Position must be less than End Position")

}
