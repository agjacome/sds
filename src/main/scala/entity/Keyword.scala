package es.uvigo.esei.tfg.smartdrugsearch.entity

final case class KeywordId (value : Long) extends AnyVal with Identifier
object KeywordId extends IdentifierCompanion[KeywordId]

final case class Keyword (
  id          : Option[KeywordId] = None,
  normalized  : Sentence,
  category    : Category,
  occurrences : Size = 0
)

object Keyword extends ((Option[KeywordId], Sentence, Category, Size) => Keyword) {

  import play.api.libs.json._
  import play.api.libs.functional.syntax._

  implicit val keywordWrites : Writes[Keyword] = (
    (__ \ 'id).writeNullable[KeywordId] and
    (__ \ 'normalized).write[Sentence]  and
    (__ \ 'category).write[Category]    and
    (__ \ 'occurrences).write[Size]
  ) (unlift(unapply))

  implicit val keywordReads : Reads[Keyword] = (
    (__ \ 'id).readNullable[KeywordId] and
    (__ \ 'normalized).read[Sentence]  and
    (__ \ 'category).read[Category]    and
    (__ \ 'occurrences).readNullable[Size].map(_ getOrElse Size(0))
  ) (apply _)

}

