package es.uvigo.ei.sing.sds.entity

import scala.slick.lifted.MappedTo

final class Position (val value : Long) extends AnyVal with Ordered[Position] with MappedTo[Long] {

  def compare(that : Position) : Int =
    value compare that.value

  def +(that : Position) : Position =
    Position(value + that.value)

  def -(that : Position) : Position =
    Position(value - that.value)

}

object Position extends (Long => Position) {

  import scala.language.implicitConversions
  import play.api.libs.json._
  import play.api.mvc.{ PathBindable, QueryStringBindable }

  def apply(pos : Long) : Position = {
    require(pos >= 0, "A Position must be a nonnegative Integer")
    new Position(pos)
  }

  implicit def longToPosition(pos : Long) : Position = Position(pos)
  implicit def positionToLong(pos : Position) : Long = pos.value

  implicit val positionWrites = Writes { (p : Position) => JsNumber(p.value) }
  implicit val positionReads  = Reads.of[Long] map apply

  implicit def bindPath(implicit binder : PathBindable[Long]) : PathBindable[Position] =
    binder transform (apply, _.value)

  implicit def bindQuery : QueryStringBindable[Position] =
    QueryStringBindable.bindableLong transform (apply, _.value)

}

