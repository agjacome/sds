package es.uvigo.esei.tfg.smartdrugsearch.model

import Category._

final case class NamedEntityId (value : Long) extends Identifier
final object     NamedEntityId extends IdentifierCompanion[NamedEntityId]

case class NamedEntity (
  val id          : Option[NamedEntityId],
  val normalized  : Sentence,
  val category    : Category,
  val occurrences : Long = 0
) {

  require(occurrences >= 0, "Number of occurrences must be non-negative")

}

