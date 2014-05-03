package es.uvigo.esei.tfg.smartdrugsearch.database.dao

import scala.language.reflectiveCalls

import es.uvigo.esei.tfg.smartdrugsearch.database.DatabaseProfile

private[dao] trait DAO[Entity <: { def id : Option[EntityId] }, EntityId] {

  protected val dbProfile = DatabaseProfile()

  import dbProfile.profile.simple.Session

  def findById(id : EntityId)(implicit session : Session) : Option[Entity]

  def findById(id : Option[EntityId])(implicit session : Session) : Option[Entity] =
    id flatMap findById

  def contains(entity : Entity)(implicit session : Session) : Boolean =
    findById(entity.id).isDefined

  def save(entity : Entity)(implicit session : Session) : EntityId =
    entity.id match {
      case Some(_) => update(entity)
      case None    => insert(entity)
    }

  protected def insert(entity : Entity)(implicit session : Session) : EntityId

  protected def update(entity : Entity)(implicit session : Session) : EntityId

  def delete(entity: Entity)(implicit session : Session) : Unit

}
