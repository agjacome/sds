package es.uvigo.esei.tfg.smartdrugsearch.entity

final case class SearchResult(document : Document, keywords : Set[Keyword])

object SearchResult extends ((Document, Set[Keyword]) => SearchResult) {

  import play.api.libs.json._
  import play.api.libs.functional.syntax._

  implicit val searchResultWrites = Json.writes[SearchResult]

}

