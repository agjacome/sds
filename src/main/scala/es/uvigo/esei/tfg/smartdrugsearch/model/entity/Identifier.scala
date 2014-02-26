package es.uvigo.esei.tfg.smartdrugsearch.model.entity

final class Identifier (val id : Int) extends AnyVal

object Identifier extends (Int => Identifier) {

  object Predef {

    import scala.language.implicitConversions

    implicit def intToIdentifier(id : Int) : Identifier =
      Identifier(id)

  }

  def apply(id : Int) : Identifier =
    if (id < 0)
      throw new IllegalArgumentException("Identifier must be a non-negative Integer")
    else new Identifier(id)

}

