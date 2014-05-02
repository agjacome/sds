package es.uvigo.esei.tfg.smartdrugsearch.entity

final case class AnnotationId (value : Long) extends AnyVal with Identifier
           object AnnotationId extends IdentifierCompanion[AnnotationId]

case class Annotation (
  id       : Option[AnnotationId] = None,
  docId    : DocumentId,
  keyId    : KeywordId,
  text     : Sentence,
  startPos : Position,
  endPos   : Position
) {

  require(startPos < endPos, "Start Position must be less than End Position")

}

