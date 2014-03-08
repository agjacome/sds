package es.uvigo.esei.tfg.smartdrugsearch.model

import play.api.mvc.{ PathBindable, QueryStringBindable }

final object Category extends Enumeration {

  import scala.language.implicitConversions

  type Category = Value

  val Compound = Value("compound")
  val Drug     = Value("drug")
  val Gene     = Value("gene")
  val Protein  = Value("protein")
  val Species  = Value("species")

  implicit def bindPath(implicit binder : PathBindable[String]) : PathBindable[Category] =
    binder transform (withName, _.toString)

  implicit def bindQuery : QueryStringBindable[Category] =
    QueryStringBindable.bindableString transform (withName, _.toString)

  implicit def stringToCategory(category : String) : Category = withName(category)
  implicit def categoryToString(category : Category) : String = category.toString

}

