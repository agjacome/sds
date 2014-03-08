package es.uvigo.esei.tfg.smartdrugsearch.model

final case class AnnotationId (value : Long) extends Identifier
final object     AnnotationId extends IdentifierCompanion[AnnotationId]

case class Annotation (
  val id       : Option[AnnotationId],
  val docId    : DocumentId,
  val entId    : NamedEntityId,
  val text     : Sentence,
  val startPos : Position,
  val endPos   : Position
) {

  require(startPos < endPos, "Start Position must be less than End Position")

}

