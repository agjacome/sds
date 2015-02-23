package es.uvigo.ei.sing.sds.entity

import scala.slick.lifted.MappedTo

private[entity] trait Identifier extends Any with MappedTo[Long]

private[entity] abstract class IdentifierCompanion[I <: Identifier] extends (Long => I) {

  import scala.language.implicitConversions
  import play.api.libs.json._
  import play.api.mvc.{ PathBindable, QueryStringBindable }

  def apply(id : Long) : I

  implicit def ordering : Ordering[I] = Ordering.by[I, Long](_.value)

  implicit def longToIdentifier(id : Long) : I = apply(id)
  implicit def identifierToLong(id : I) : Long = id.value

  implicit val identifierWrites = Writes { (i : I) => JsNumber(i.value) }
  implicit val identifierReads  = Reads.of[Long] map apply

  implicit def bindPath(implicit binder : PathBindable[Long]) : PathBindable[I] =
    binder transform (apply, _.value)

  implicit def bindQuery : QueryStringBindable[I] =
    QueryStringBindable.bindableLong transform (apply, _.value)

}

