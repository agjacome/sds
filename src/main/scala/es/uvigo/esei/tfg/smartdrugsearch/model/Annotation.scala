package es.uvigo.esei.tfg.smartdrugsearch.model

import org.virtuslab.unicorn.ids._

import Category._

final case class AnnotationId (id : Long) extends AnyVal with BaseId

object AnnotationId extends IdCompanion[AnnotationId] {

  object Predef {
    import scala.language.implicitConversions

    implicit def longToAnnotationId(id : Long) : AnnotationId = AnnotationId(id)
    implicit def annotationIdToLong(id : AnnotationId) : Long = id.id
  }

}

case class Annotation (
  val id       : Option[AnnotationId],
  val docId    : DocumentId,
  val entId    : NamedEntityId,
  val text     : Sentence,
  val startPos : Position,
  val endPos   : Position
) extends WithId[AnnotationId] {

  require(startPos < endPos, "Start Position must be less than End Position")

}

