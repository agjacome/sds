package es.uvigo.esei.tfg.smartdrugsearch.database.dao

import es.uvigo.esei.tfg.smartdrugsearch.database.DatabaseProfile
import es.uvigo.esei.tfg.smartdrugsearch.entity.{ Document, DocumentId }

trait DocumentsDAO extends DAO[Document, DocumentId]

object DocumentsDAO extends (() => DocumentsDAO) with ((DatabaseProfile) => DocumentsDAO) {

  def apply : DocumentsDAO =
    new DocumentsDAOImpl

  def apply(db : DatabaseProfile) : DocumentsDAO =
    new DocumentsDAOImpl(db)

}

private class DocumentsDAOImpl (val db : DatabaseProfile = DatabaseProfile()) extends DocumentsDAO {

  import db._
  import db.profile._
  import db.profile.simple._

  private val documents = TableQuery[DocumentsTable]

  override def findById(id : DocumentId)(implicit session : Session) : Option[Document] =
    (documents where (_.id is id)).firstOption

  protected def insert(document : Document)(implicit session : Session) : Document =
    document copy (id = Some(documents returning (documents map (_.id)) += document))

  protected def update(document : Document)(implicit session : Session) : Document = {
    documents where (_.id is document.id.get) update (document)
    document
  }

  def delete(document : Document)(implicit session : Session) : Unit =
    (documents where (_.id is document.id.get)).delete

}

