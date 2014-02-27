package es.uvigo.esei.tfg.smartdrugsearch.model

import org.virtuslab.unicorn.ids._
import scala.slick.session.Session

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

object NamedEntities extends IdTable[NamedEntityId, NamedEntity]("named_entities") {

  import Category.Predef._
  import Sentence.Predef._

  def normalized  = column[Sentence]("text", O.NotNull)
  def category    = column[Category]("category", O.NotNull)
  def occurrences = column[Long]("counter", O.Default(0))

  private def base = normalized ~ category ~ occurrences

  override def * = id.? ~: base <> (NamedEntity.apply _, NamedEntity.unapply _)

  override def insertOne(namedEnt : NamedEntity)(implicit session : Session) : NamedEntityId =
    saveBase(base, NamedEntity.unapply _)(namedEnt)

}

