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

object Keyword extends ((Option[KeywordId], Sentence, Category, Long) => Keyword) {

  import play.api.libs.json._
  import play.api.libs.functional.syntax._

  implicit val keywordWrites : Writes[Keyword] = (
    (__ \ 'id).writeNullable[KeywordId] and
    (__ \ 'normalized).write[Sentence]  and
    (__ \ 'category).write[Category]    and
    (__ \ 'occurrences).write[Long]
  ) (unlift(unapply))

  implicit val keywordReads : Reads[Keyword] = (
    (__ \ 'id).readNullable[KeywordId] and
    (__ \ 'normalized).read[Sentence]  and
    (__ \ 'category).read[Category]    and
    (__ \ 'occurrences).readNullable[Long].map(_ getOrElse 0L)
  ) (apply _)

}

