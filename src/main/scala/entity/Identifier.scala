package es.uvigo.esei.tfg.smartdrugsearch.entity

import scala.slick.lifted.MappedTo

import play.api.mvc.{ PathBindable, QueryStringBindable }

private[entity] trait Identifier extends MappedTo[Long]

private[entity] abstract class IdentifierCompanion[I <: Identifier] extends (Long => I) {

  import scala.language.implicitConversions

  def apply(id : Long) : I

  implicit def ordering : Ordering[I] = Ordering.by[I, Long](_.value)

  implicit def bindPath(implicit binder : PathBindable[Long]) : PathBindable[I] =
    binder transform (apply, _.value)

  implicit def bindQuery : QueryStringBindable[I] =
    QueryStringBindable.bindableLong transform (apply, _.value)

  implicit def longToIdentifier(id : Long) : I = apply(id)
  implicit def identifierToLong(id : I) : Long = id.value

}

