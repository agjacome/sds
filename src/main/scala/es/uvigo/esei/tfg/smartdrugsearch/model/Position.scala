package es.uvigo.esei.tfg.smartdrugsearch.model

import scala.slick.lifted.MappedTo
import play.api.mvc.{ PathBindable, QueryStringBindable }

final class Position (val value : Long) extends AnyVal with Ordered[Position] with MappedTo[Long] {

  def compare(that : Position) : Int =
    value compare that.value

}

final object Position extends (Long => Position) {

  import scala.language.implicitConversions

  def apply(pos : Long) : Position = {
    require(pos >= 0, "A Position must be a nonnegative Integer")
    new Position(pos)
  }

  implicit def bindPath(implicit binder : PathBindable[Long]) : PathBindable[Position] =
    binder transform (apply, _.value)

  implicit def bindQuery : QueryStringBindable[Position] =
    QueryStringBindable.bindableLong transform (apply, _.value)

  implicit def longToPosition(pos : Long) : Position = Position(pos)
  implicit def positionToLong(pos : Position) : Long = pos.value

}

