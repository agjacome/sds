package es.uvigo.esei.tfg.smartdrugsearch.entity

final case class SearchResult(document : Document, keywords : Set[Keyword])
object SearchResult extends ((Document, Set[Keyword]) => SearchResult) {

  import play.api.libs.json._
  import play.api.libs.functional.syntax._

  implicit val searchResultWrites = Json.writes[SearchResult]

}

final case class SearchResults(
  totalCount : Size,
  pageNumber : Position,
  pageSize   : Size,
  results    : Seq[SearchResult]
)

object SearchResults extends ((Size, Position, Size, Seq[SearchResult]) => SearchResults) {

  import play.api.libs.json._
  import play.api.libs.functional.syntax._

  implicit val searchResultsWrites = Json.writes[SearchResults]

}

