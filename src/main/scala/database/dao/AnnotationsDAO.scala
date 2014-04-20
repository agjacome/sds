package es.uvigo.esei.tfg.smartdrugsearch.database.dao

import es.uvigo.esei.tfg.smartdrugsearch.database.DatabaseProfile
import es.uvigo.esei.tfg.smartdrugsearch.entity.{ Annotation, AnnotationId }

trait AnnotationsDAO extends DAO[Annotation, AnnotationId]

object AnnotationsDAO extends (() => AnnotationsDAO) {

  def apply : AnnotationsDAO =
    new AnnotationsDAOImpl

}

private class AnnotationsDAOImpl extends AnnotationsDAO {

  import databaseProfile._
  import databaseProfile.profile.simple._

  override def findById(id : AnnotationId)(implicit sesion : Session) : Option[Annotation] =
    (Annotations filter (_.id is id)).firstOption

  protected def insert(annotation : Annotation)(implicit session : Session) : Annotation = {
    val id = Some(Annotations returning (Annotations map (_.id)) += annotation)
    annotation copy id
  }

  protected def update(annotation : Annotation)(implicit session : Session) : Annotation = {
    Annotations filter (_.id is annotation.id.get) update (annotation)
    annotation
  }

  def delete(annotation : Annotation)(implicit session : Session) : Unit =
    (Annotations filter (_.id is annotation.id.get)).delete

}

