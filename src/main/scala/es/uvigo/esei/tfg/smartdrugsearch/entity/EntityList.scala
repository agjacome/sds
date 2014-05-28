package es.uvigo.esei.tfg.smartdrugsearch.entity

import play.api.libs.json._
import play.api.libs.functional.syntax._

private[entity] trait EntityList[E] {
  val totalCount : Size
  val pageNumber : Position
  val pageSize   : Size
  val list       : Seq[E]
}

