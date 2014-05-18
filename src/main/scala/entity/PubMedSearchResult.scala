package es.uvigo.esei.tfg.smartdrugsearch.entity

final case class PubMedSearchResult(
  totalResults : Size,
  firstElement : Position,
  idList       : Set[PubMedId]
)

object PubMedSearchResult extends ((Size, Position, Set[PubMedId]) => PubMedSearchResult) {

  import play.api.libs.json._
  import play.api.libs.functional.syntax._

  implicit val searchResultWrites : Writes[PubMedSearchResult] = (
    (__ \ 'count).write[Size]     and
    (__ \ 'first).write[Position] and
    (__ \ 'results).write[Set[PubMedId]]
  ) (unlift(unapply))

}

