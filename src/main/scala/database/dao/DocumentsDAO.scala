package es.uvigo.esei.tfg.smartdrugsearch.database.dao

import es.uvigo.esei.tfg.smartdrugsearch.database.DatabaseProfile
import es.uvigo.esei.tfg.smartdrugsearch.entity.{ Document, DocumentId }

trait DocumentsDAO extends DAO[Document, DocumentId]

object DocumentsDAO extends (() => DocumentsDAO) {

  def apply : DocumentsDAO =
    new DocumentsDAOImpl

}

private class DocumentsDAOImpl extends DocumentsDAO {

  import databaseProfile._
  import databaseProfile.profile.simple._

  override def findById(id : DocumentId)(implicit session : Session) : Option[Document] =
    (Documents filter (_.id is id)).firstOption

  protected def insert(document : Document)(implicit session : Session) : Document = {
    val id = Some(Documents returning (Documents map (_.id)) += document)
    document copy id
  }

  protected def update(document : Document)(implicit session : Session) : Document = {
    Documents filter (_.id is document.id.get) update (document)
    document
  }

  def delete(document : Document)(implicit session : Session) : Unit =
    (Documents filter (_.id is document.id.get)).delete

}

