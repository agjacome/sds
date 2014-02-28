package es.uvigo.esei.tfg.smartdrugsearch.model.database

import scala.slick.session.Session
import org.virtuslab.unicorn.ids._

import es.uvigo.esei.tfg.smartdrugsearch.model._

object Annotations extends IdTable[AnnotationId, Annotation]("annotations") {

  import Category._
  import Category.Predef._
  import Position.Predef._
  import Sentence.Predef._

  def documentId    = column[DocumentId]("document", O.NotNull)
  def namedEntityId = column[NamedEntityId]("named_entity", O.NotNull)
  def originalText  = column[Sentence]("text", O.NotNull)
  def startPosition = column[Position]("start", O.NotNull)
  def endPosition   = column[Position]("end", O.NotNull)

  def document    = foreignKey("Annotation_Document_FK", documentId, Documents)(_.id)
  def namedEntity = foreignKey("Annotation_NamedEntity_FL", namedEntityId, NamedEntities)(_.id)

  private def base = documentId ~ namedEntityId ~ originalText ~ startPosition ~ endPosition

  override def * = id.? ~: base <> (Annotation.apply _, Annotation.unapply _)

  override def insertOne(annot : Annotation)(implicit session : Session) : AnnotationId =
    saveBase(base, Annotation.unapply _)(annot)

}

