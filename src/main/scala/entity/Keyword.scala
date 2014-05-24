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

  implicit val keywordFormat = Json.format[Keyword]

}

