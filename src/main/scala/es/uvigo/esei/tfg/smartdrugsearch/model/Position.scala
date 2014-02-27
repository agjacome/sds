package es.uvigo.esei.tfg.smartdrugsearch.model

final class Position private (val pos : Long) extends AnyVal with Ordered[Position] {

  def compare(that : Position) : Int = this.pos compare (that.pos)

}

object Position extends (Long => Position) {

  object Predef {

    import scala.language.implicitConversions
    import scala.slick.lifted.{ TypeMapper, MappedTypeMapper }

    implicit def longToPosition(pos : Long)     : Position = Position(pos)
    implicit def positionToLong(pos : Position) : Long     = pos.pos

    implicit val positionTypeMapper : TypeMapper[Position] =
      MappedTypeMapper.base[Position, Long](positionToLong, longToPosition)

  }

  def apply(pos : Long) : Position = {
    require(pos >= 0, "A Position must be a nonnegative Integer")
    new Position(pos)
  }

}

