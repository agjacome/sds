package es.uvigo.esei.tfg.smartdrugsearch.entity

final case class KeywordId (value : Long) extends Identifier
final object     KeywordId extends IdentifierCompanion[KeywordId]

case class Keyword (
  val id          : Option[KeywordId] = None,
  val normalized  : Sentence,
  val category    : Category,
  val occurrences : Long = 0
) {

  require(occurrences >= 0, "Number of occurrences must be non-negative")

}

