package es.uvigo.esei.tfg.smartdrugsearch.database.dao

import es.uvigo.esei.tfg.smartdrugsearch.database.DatabaseProfile
import es.uvigo.esei.tfg.smartdrugsearch.entity.{ Annotation, AnnotationId }

trait AnnotationsDAO extends DAO[Annotation, AnnotationId]

object AnnotationsDAO extends (() => AnnotationsDAO) with ((DatabaseProfile) => AnnotationsDAO) {

  def apply : AnnotationsDAO =
    new AnnotationsDAOImpl

  def apply(db : DatabaseProfile) : AnnotationsDAO =
    new AnnotationsDAOImpl(db)

}

private class AnnotationsDAOImpl (val db : DatabaseProfile = DatabaseProfile()) extends AnnotationsDAO {

  import db._
  import db.profile._
  import db.profile.simple._

  private val annotations = TableQuery[AnnotationsTable]

  override def findById(id : AnnotationId)(implicit session : Session) : Option[Annotation] =
    (annotations where (_.id is id)).firstOption

  protected def insert(annotation : Annotation)(implicit session : Session) : Annotation =
    annotation copy (id = Some(annotations returning (annotations map (_.id)) += annotation))

  protected def update(annotation : Annotation)(implicit session : Session) : Annotation = {
    annotations where (_.id is annotation.id.get) update (annotation)
    annotation
  }

  def delete(annotation : Annotation)(implicit session : Session) : Unit =
    (annotations where (_.id is annotation.id.get)).delete

}
