package es.uvigo.esei.tfg.smartdrugsearch.entity

import es.uvigo.esei.tfg.smartdrugsearch.macros.SealedValues

sealed trait Category { val id : Int }

case object Compound extends Category { val id = 1 }
case object Drug     extends Category { val id = 2 }
case object Gene     extends Category { val id = 3 }
case object Protein  extends Category { val id = 4 }
case object Species  extends Category { val id = 5 }

object Category {

  private lazy val values : Set[Category] = SealedValues.from[Category]

  private lazy val fromInt : Map[Int   , Category] = (values map { v => (v.id, v) }).toMap
  private lazy val fromStr : Map[String, Category] = (values map { v => (v.toString.toLowerCase, v) }).toMap

  def apply(id : Int) : Category =
    fromInt(id)

  def apply(str : String) : Category =
    fromStr(str.toLowerCase)

}
