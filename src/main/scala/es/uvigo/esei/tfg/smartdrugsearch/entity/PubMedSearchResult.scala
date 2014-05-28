package es.uvigo.esei.tfg.smartdrugsearch.entity

final case class PubMedSearchResult(
  totalResults : Size,
  firstElement : Position,
  idList       : Set[PubMedId]
)

object PubMedSearchResult extends ((Size, Position, Set[PubMedId]) => PubMedSearchResult) {

  import play.api.libs.json._
  import play.api.libs.functional.syntax._

  implicit val pubmedSearchResultWrites = Json.writes[PubMedSearchResult]

}

