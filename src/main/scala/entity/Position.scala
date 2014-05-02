package es.uvigo.esei.tfg.smartdrugsearch.entity

import scala.slick.lifted.MappedTo

final class Position (val value : Long) extends AnyVal with Ordered[Position] with MappedTo[Long] {

  def compare(that : Position) : Int =
    value compare that.value

}

object Position extends (Long => Position) {

  import scala.language.implicitConversions

  def apply(pos : Long) : Position = {
    require(pos >= 0, "A Position must be a nonnegative Integer")
    new Position(pos)
  }

  implicit def longToPosition(pos : Long) : Position = Position(pos)
  implicit def positionToLong(pos : Position) : Long = pos.value

}

