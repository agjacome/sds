package es.uvigo.esei.tfg.smartdrugsearch.model

import scala.slick.session.Session
import org.virtuslab.unicorn.ids._

import Category._

case class AnnotationId (id : Long) extends AnyVal with BaseId

object AnnotationId extends IdCompanion[AnnotationId] {

  object Predef {
    import scala.language.implicitConversions

    implicit def longToAnnotationId(id : Long) : AnnotationId = AnnotationId(id)
    implicit def annotationIdToLong(id : AnnotationId) : Long = id.id
  }

}

case class Annotation (
  val id             : Option[AnnotationId],
  val originalText   : Sentence,
  val normalizedText : Sentence,
  val category       : Category,
  val documentId     : DocumentId,
  val startPosition  : Position,
  val endPosition    : Position
) extends WithId[AnnotationId] {

  require(
    startPosition < endPosition,
    "Start Position must be less than End Position"
  )

}

object Annotations extends IdTable[AnnotationId, Annotation]("Annotations") {

  import Category.Predef._
  import Position.Predef._
  import Sentence.Predef._

  def originalText   = column[Sentence]("Text", O.NotNull)
  def normalizedText = column[Sentence]("Normalized", O.NotNull)
  def category       = column[Category]("Category", O.NotNull)
  def documentId     = column[DocumentId]("Document", O.NotNull)
  def startPosition  = column[Position]("Start", O.NotNull)
  def endPosition    = column[Position]("End", O.NotNull)

  def document = foreignKey("Annotation_Document_FK", documentId, Documents)(_.id)

  private def base =
    originalText ~ normalizedText ~ category ~ documentId ~ startPosition ~ endPosition

  override def * = id.? ~: base <> (Annotation.apply _, Annotation.unapply _)

  override def insertOne(annot : Annotation)(implicit session : Session) : AnnotationId =
    saveBase(base, Annotation.unapply _)(annot)

}

