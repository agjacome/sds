package es.uvigo.ei.sing.sds.entity

final case class SearchResult(document : Document, keywords : Set[Keyword])

object SearchResult extends ((Document, Set[Keyword]) => SearchResult) {

  import play.api.libs.json._

  implicit val searchResultWrites = Json.writes[SearchResult]

}

