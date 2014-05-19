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

final case class SearchResults(totalCount : Size, firstElement : Position, results : Set[SearchResult])
object SearchResults extends ((Size, Position, Set[SearchResult]) => SearchResults) {

  import play.api.libs.json._
  import play.api.libs.functional.syntax._

  implicit val searchResultsWrites : Writes[SearchResults] = (
    (__ \ 'totalCount).write[Size]       and
    (__ \ 'firstElement).write[Position] and
    (__ \ 'results).write[Set[SearchResult]]
  ) (unlift(unapply))

}

