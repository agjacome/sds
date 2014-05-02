package es.uvigo.esei.tfg.smartdrugsearch.entity

final case class KeywordId (value : Long) extends AnyVal with Identifier
           object KeywordId extends IdentifierCompanion[KeywordId]

case class Keyword (
  id          : Option[KeywordId] = None,
  normalized  : Sentence,
  category    : Category,
  occurrences : Long = 0
) {

  require(occurrences >= 0, "Number of occurrences must be non-negative")

}

