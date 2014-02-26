package es.uvigo.esei.tfg.smartdrugsearch.model.entity

import Category._

final case class Annotation (
  val id             : Identifier,
  val originalText   : Sentence,
  val normalizedText : Sentence,
  val category       : Category,
  val document       : Document,
  val startPosition  : Position,
  val endPosition    : Position
) {

  require(
    startPosition < endPosition,
    "Start Position must be less than End Position"
  )

  require(
    endPosition.pos <= document.text.size,
    "End Position must be in the range of the document"
  )

}

