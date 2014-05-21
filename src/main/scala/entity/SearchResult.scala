package es.uvigo.esei.tfg.smartdrugsearch.entity

final case class SearchResult(document : Document, keywords : Set[Keyword])
object SearchResult extends ((Document, Set[Keyword]) => SearchResult) {

  import play.api.libs.json._
  import play.api.libs.functional.syntax._

  implicit val searchResultWrites : Writes[SearchResult] = (
    (__ \ 'document).write[Document] and
    (__ \ 'keywords).write[Set[Keyword]]
  ) (unlift(unapply))

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

  implicit val searchResultsWrites : Writes[SearchResults] = (
    (__ \ 'totalCount).write[Size]     and
    (__ \ 'pageNumber).write[Position] and
    (__ \ 'pageSize).write[Size]       and
    (__ \ 'results).write[Seq[SearchResult]]
  ) (unlift(unapply))

}

