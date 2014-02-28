package es.uvigo.esei.tfg.smartdrugsearch.model.database

import scala.slick.session.Session
import org.virtuslab.unicorn.ids._

import es.uvigo.esei.tfg.smartdrugsearch.model.{ NamedEntity, NamedEntityId, Sentence, Category }

object NamedEntities extends IdTable[NamedEntityId, NamedEntity]("named_entities") {

  import Category._
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

