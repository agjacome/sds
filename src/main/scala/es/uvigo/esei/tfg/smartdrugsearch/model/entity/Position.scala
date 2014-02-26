package es.uvigo.esei.tfg.smartdrugsearch.model.entity

final class Position private (val pos : Int) extends AnyVal with Ordered[Position] {

  def compare(that : Position) : Int =
    this.pos - that.pos

}

object Position extends (Int => Position) {

  object Predef {

    import scala.language.implicitConversions

    implicit def intToPosition(pos : Int) : Position =
      Position(pos)

  }

  def apply(pos : Int) : Position =
    if (pos >= 0)
      new Position(pos)
    else
      throw new IllegalArgumentException("A Position must be a nonnegative Integer")

}
