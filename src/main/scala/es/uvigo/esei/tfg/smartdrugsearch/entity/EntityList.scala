package es.uvigo.esei.tfg.smartdrugsearch.entity

import play.api.libs.json._
import play.api.libs.functional.syntax._

sealed trait EntityList[A] {
  val totalCount : Size
  val pageNumber : Position
  val pageSize   : Size
  val list       : Seq[A]
}

case class DocumentList (
  totalCount : Size, pageNumber : Position, pageSize : Size, list : Seq[Document]
) extends EntityList[Document]

case class AccountList (
  totalCount : Size, pageNumber : Position, pageSize : Size, list : Seq[Account]
) extends EntityList[Account]

case class PubMedIdList (
  totalCount : Size, pageNumber : Position, pageSize : Size, list : Seq[PubMedId]
) extends EntityList[PubMedId]

case class SearchResultList (
  totalCount : Size, pageNumber : Position, pageSize : Size, list : Seq[SearchResult]
) extends EntityList[SearchResult]

object EntityList {

  implicit val documentListWrites     = Json.writes[DocumentList]
  implicit val accountListWirtes      = Json.writes[AccountList]
  implicit val pubmedIdListWrites     = Json.writes[PubMedIdList]
  implicit val searchResultListWrites = Json.writes[SearchResultList]

}

