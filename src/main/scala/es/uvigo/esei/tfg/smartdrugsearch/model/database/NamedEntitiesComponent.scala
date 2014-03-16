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

  implicit class NamedEntitiesExtension(val query : Query[NamedEntitiesTable, NamedEntity]) {

    def byId(id : NamedEntityId) : Query[NamedEntitiesTable, NamedEntity] =
      query filter (_.id is id)

    def byNormalized(normalized : Sentence) : Query[NamedEntitiesTable, NamedEntity] =
      query filter (_.normalized is normalized)

    def byCategory(category : Category) : Query[NamedEntitiesTable, NamedEntity] =
      query filter (_.category is category)

    def byOccurrences(occurrences : Long) : Query[NamedEntitiesTable, NamedEntity] =
      query filter (_.occurrences is occurrences)

    def byNormalizedLike(filter : String =  "%") : Query[NamedEntitiesTable, NamedEntity] =
      query filter (_.normalized.asColumnOf[String] like s"%${filter}%")

    def contains(entity : NamedEntity)(implicit session : Session) : Boolean =
      entity.id.isDefined && byId(entity.id.get).map(_.id).exists.run

  }

}

