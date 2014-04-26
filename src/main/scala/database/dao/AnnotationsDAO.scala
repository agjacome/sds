package es.uvigo.esei.tfg.smartdrugsearch.database.dao

import es.uvigo.esei.tfg.smartdrugsearch.database.DatabaseProfile
import es.uvigo.esei.tfg.smartdrugsearch.entity._

trait AnnotationsDAO extends DAO[Annotation, AnnotationId] {

  import databaseProfile.profile.simple.Session

  def documentFor(id : AnnotationId)(implicit session : Session) : Option[Document]

  def documentFor(id : Option[AnnotationId])(implicit session : Session) : Option[Document] =
    id flatMap documentFor

  def documentFor(annotation : Annotation)(implicit session : Session) : Option[Document] =
    annotation.id flatMap documentFor

  def keywordFor(id : AnnotationId)(implicit session : Session) : Option[Keyword]

  def keywordFor(id : Option[AnnotationId])(implicit session : Session) : Option[Keyword] =
    id flatMap keywordFor

  def keywordFor(annotation : Annotation)(implicit session : Session) : Option[Keyword] =
    annotation.id flatMap keywordFor

  def findByDocumentId(id : DocumentId)(implicit session : Session) : Seq[Annotation]

  def findByDocumentId(id : Option[DocumentId])(implicit session : Session) : Seq[Annotation] =
    id match {
      case Some(id) => findByDocumentId(id)
      case None     => Seq.empty
    }

  def findByDocument(document : Document)(implicit session : Session) : Seq[Annotation] =
    findByDocumentId(document.id)

  def findByKeywordId(id : KeywordId)(implicit session : Session) : Seq[Annotation]

  def findByKeywordId(id : Option[KeywordId])(implicit session : Session) : Seq[Annotation] =
    id match {
      case Some(id) => findByKeywordId(id)
      case None     => Seq.empty
    }

  def findByKeyword(keyword : Keyword)(implicit session : Session) : Seq[Annotation] =
    findByKeywordId(keyword.id)

}

object AnnotationsDAO extends (() => AnnotationsDAO) {

  def apply : AnnotationsDAO = new AnnotationsDAOImpl

}

private class AnnotationsDAOImpl extends AnnotationsDAO {

  import databaseProfile._
  import databaseProfile.profile.simple._

  def documentFor(id : AnnotationId)(implicit session : Session) : Option[Document] =
    (for { a <- Annotations if (a.id is id); d <- a.document } yield d).firstOption

  def keywordFor(id : AnnotationId)(implicit session : Session) : Option[Keyword] =
    (for { a <- Annotations if (a.id is id); k <- a.keyword } yield k).firstOption

  override def findById(id : AnnotationId)(implicit sesion : Session) : Option[Annotation] =
    (Annotations filter (_.id is id)).firstOption

  def findByDocumentId(id : DocumentId)(implicit session : Session) : Seq[Annotation] =
    (for { a <- Annotations; d <- a.document if (d.id is id) } yield a).run

  def findByKeywordId(id : KeywordId)(implicit session : Session) : Seq[Annotation] =
    (for { a <- Annotations; k <- a.keyword if (k.id is id) } yield a).run

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
