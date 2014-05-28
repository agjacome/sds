package es.uvigo.esei.tfg.smartdrugsearch.entity

import play.api.libs.json._
import play.api.mvc.{ PathBindable, QueryStringBindable }

import es.uvigo.esei.tfg.smartdrugsearch.macros.SealedValues

final case class CategoryId (value : Long) extends AnyVal with Identifier
object CategoryId extends IdentifierCompanion[CategoryId]

sealed trait Category { val id : CategoryId }

case object Compound extends Category { val id = CategoryId(1) }
case object Drug     extends Category { val id = CategoryId(2) }
case object Gene     extends Category { val id = CategoryId(3) }
case object Protein  extends Category { val id = CategoryId(4) }
case object Species  extends Category { val id = CategoryId(5) }
case object DNA      extends Category { val id = CategoryId(6) }
case object RNA      extends Category { val id = CategoryId(7) }
case object CellLine extends Category { val id = CategoryId(8) }
case object CellType extends Category { val id = CategoryId(9) }

object Category extends ((String) => Category) {

  private lazy val values  = SealedValues.from[Category]
  private lazy val fromId  = (values map { v => (v.id, v) }).toMap
  private lazy val fromStr = (values map { v => (v.toString.toLowerCase, v) }).toMap

  def apply(id : CategoryId) : Category =
    fromId(id)

  def apply(str : String) : Category =
    fromStr(str.toLowerCase)

  implicit val sentenceWrites = Writes { (c : Category) => JsString(c.toString) }
  implicit val sentenceReads  = Reads.of[String] map apply

  implicit def bindPath(implicit binder : PathBindable[String]) : PathBindable[Category] =
    binder transform (apply, _.toString.toLowerCase)

  implicit def bindQuery : QueryStringBindable[Category] =
    QueryStringBindable.bindableString transform (apply, _.toString.toLowerCase)

}

