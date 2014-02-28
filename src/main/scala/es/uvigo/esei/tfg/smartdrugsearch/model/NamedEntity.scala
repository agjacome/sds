package es.uvigo.esei.tfg.smartdrugsearch.model

import org.virtuslab.unicorn.ids._

import Category._

final case class NamedEntityId (val id : Long) extends AnyVal with BaseId

object NamedEntityId extends IdCompanion[NamedEntityId] {

  object Predef {
    import scala.language.implicitConversions

    implicit def longToNamedEntityId(id : Long) : NamedEntityId = NamedEntityId(id)
    implicit def namedEntityIdToLong(id : NamedEntityId) : Long = id.id
  }

}

case class NamedEntity (
  val id          : Option[NamedEntityId],
  val normalized  : Sentence,
  val category    : Category,
  val occurrences : Long = 0
) extends WithId[NamedEntityId] {

  require(occurrences >= 0, "Number of occurrences must be non-negative")

}

