package es.uvigo.esei.tfg.smartdrugsearch.database.dao

import es.uvigo.esei.tfg.smartdrugsearch.database.DatabaseProfile

private[dao] trait DAO[Entity <: { def id : Option[EntityId] }, EntityId] {

  val db : DatabaseProfile

  import db.profile._
  import db.profile.simple._
  import scala.language.reflectiveCalls

  def findById(id : Option[EntityId])(implicit session : Session) : Option[Entity] =
    id match {
      case Some(i) => findById(i)
      case None    => None
    }

  def contains(entity : Entity)(implicit session : Session) : Boolean =
    findById(entity.id).isDefined

  def save(entity : Entity)(implicit session : Session) : Entity =
    entity.id match {
      case Some(_) => update(entity)
      case None    => insert(entity)
    }

  protected def insert(entity : Entity)(implicit session : Session) : Entity

  protected def update(entity : Entity)(implicit session : Session) : Entity

  def findById(id : EntityId)(implicit session : Session) : Option[Entity]

  def delete(entity: Entity)(implicit session : Session) : Unit

}

