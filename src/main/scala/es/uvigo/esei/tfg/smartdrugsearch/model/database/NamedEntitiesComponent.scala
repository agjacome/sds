package es.uvigo.esei.tfg.smartdrugsearch.model.database

import play.api.db.slick.Profile
import es.uvigo.esei.tfg.smartdrugsearch.model.{ Category, NamedEntity, NamedEntityId, Sentence }
import Category._

private[database] trait NamedEntitiesComponent {

  this : Profile with Mappers =>

  import profile.simple._

  class NamedEntitiesTable(val tag : Tag) extends Table[NamedEntity](tag, "entities") {

    def id          = column[NamedEntityId]("id", O.PrimaryKey, O.AutoInc)
    def normalized  = column[Sentence]("text", O.NotNull)
    def category    = column[Category]("category", O.NotNull)
    def occurrences = column[Long]("counter", O.Default(0))

    def * = (id.?, normalized, category, occurrences) <> (NamedEntity.tupled, NamedEntity.unapply)

  }

}

