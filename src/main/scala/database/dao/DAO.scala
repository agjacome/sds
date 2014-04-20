package es.uvigo.esei.tfg.smartdrugsearch.database.dao

import scala.language.reflectiveCalls
import play.api.db.slick.Database

import es.uvigo.esei.tfg.smartdrugsearch.database.DatabaseProfile

private[dao] trait DAO[Entity <: { def id : Option[EntityId] }, EntityId] {

  protected val databaseProfile = DatabaseProfile()

  import databaseProfile.profile.simple._

  def findById(id : EntityId)(implicit session : Session) : Option[Entity]

  def findById(id : Option[EntityId])(implicit session : Session) : Option[Entity] =
    id flatMap findById

  def contains(entity : Entity)(implicit session : Session) : Boolean =
    findById(entity.id).isDefined

  def save(entity : Entity)(implicit session : Session) : Entity =
    entity.id match {
      case Some(_) => update(entity)
      case None    => insert(entity)
    }

  protected def insert(entity : Entity)(implicit session : Session) : Entity

  protected def update(entity : Entity)(implicit session : Session) : Entity

  def delete(entity: Entity)(implicit session : Session) : Unit

}

