package es.uvigo.esei.tfg.smartdrugsearch.database.dao

import es.uvigo.esei.tfg.smartdrugsearch.entity.{ Document, DocumentId, PubMedId }

trait DocumentsDAO extends DAO[Document, DocumentId] {

  def findByPubMedId(id : PubMedId)(implicit session : Session) : Option[Document]

  def findByPubMedId(id : Option[PubMedId])(implicit session : Session) : Option[Document] =
    id flatMap findByPubMedId

}

object DocumentsDAO extends (() => DocumentsDAO) {

  def apply( ) : DocumentsDAO =
    new DocumentsDAOImpl

}

private class DocumentsDAOImpl extends DocumentsDAO {

  import dbProfile._
  import dbProfile.profile.simple._

  override def findById(id : DocumentId)(implicit session : Session) : Option[Document] =
    (Documents filter (_.id is id)).firstOption

  def findByPubMedId(id : PubMedId)(implicit session : Session) : Option[Document] =
    (Documents filter (_.pubmedId is id)).firstOption

  def delete(document : Document)(implicit session : Session) : Unit =
    (Documents filter (_.id is document.id.get)).delete

  protected def insert(document : Document)(implicit session : Session) =
    Documents returning (Documents map (_.id)) += document

  protected def update(document : Document)(implicit session : Session) = {
    Documents filter (_.id is document.id.get) update document
    document.id.get
  }

}
