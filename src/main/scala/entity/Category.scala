package es.uvigo.esei.tfg.smartdrugsearch.entity

import play.api.libs.json._
import play.api.mvc.{ PathBindable, QueryStringBindable }

import es.uvigo.esei.tfg.smartdrugsearch.macros.SealedValues

sealed trait Category { val id : Int }

case object Compound extends Category { val id = 1 }
case object Drug     extends Category { val id = 2 }
case object Gene     extends Category { val id = 3 }
case object Protein  extends Category { val id = 4 }
case object Species  extends Category { val id = 5 }
case object DNA      extends Category { val id = 6 }
case object RNA      extends Category { val id = 7 }
case object CellLine extends Category { val id = 8 }
case object CellType extends Category { val id = 9 }

object Category extends ((String) => Category) {

  private lazy val values  = SealedValues.from[Category]
  private lazy val fromInt = (values map { v => (v.id, v) }).toMap
  private lazy val fromStr = (values map { v => (v.toString.toLowerCase, v) }).toMap

  def apply(id : Int) : Category =
    fromInt(id)

  def apply(str : String) : Category =
    fromStr(str.toLowerCase)

  implicit val sentenceWrites : Writes[Category] = Writes { (c : Category) => JsString(c.toString) }
  implicit val sentenceReads  : Reads[Category]  = Reads.of[String] map apply

  implicit def bindPath(implicit binder : PathBindable[String]) : PathBindable[Category] =
    binder transform (apply, _.toString.toLowerCase)

  implicit def bindQuery : QueryStringBindable[Category] =
    QueryStringBindable.bindableString transform (apply, _.toString.toLowerCase)

}

