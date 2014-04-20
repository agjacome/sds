package es.uvigo.esei.tfg.smartdrugsearch.annotator

import scala.collection.JavaConversions._
import scala.concurrent.{ Future, future }

import uk.ac.cam.ch.wwmm.oscar.Oscar
import uk.ac.cam.ch.wwmm.oscar.chemnamedict.entities.{ FormatType, ResolvedNamedEntity }
import uk.ac.cam.ch.wwmm.oscar.document.NamedEntity

import es.uvigo.esei.tfg.smartdrugsearch.entity._

private[annotator] class OscarNERAdapter extends NERAdapter {

  import context._
  import dbProfile.database
  import OscarNERAdapter._

  override protected def annotate(document : Document) : Future[Finished] =
    getNamedEntities(document.text) map { entities =>
      entities foreach { saveEntity(_, document) }
      Finished(document)
    }

  private[this] def getNamedEntities(text : String) : Future[Seq[ResolvedNamedEntity]] =
    future { oscar.findResolvableEntities(text) }

  private[this] def saveEntity(entity : ResolvedNamedEntity, document : Document) : Unit = {
    val keyword    = getKeyword(entity.getFirstChemicalStructure(FormatType.INCHI).getValue)
    val annotation = getAnnotation(entity.getNamedEntity, keyword, document)
    insertAnnotation(keyword, annotation)
  }

  private[this] def getAnnotation(e : NamedEntity, k : Keyword, d : Document) : Annotation =
    Annotation(None, d.id.get, k.id.get, e.getSurface, e.getStart, e.getEnd)

  private[this] def getKeyword(inchi : String) : Keyword =
    database withTransaction { implicit session =>
      (Keywords findByNormalized inchi) getOrElse (
        Keywords save Keyword(None, inchi, Compound)
      )
    }

  private[this] def insertAnnotation(keyword : Keyword, annotation : Annotation) : Unit =
    database withTransaction { implicit session =>
      val current = (Keywords findById keyword.id).get
      Annotations save annotation
      Keywords save (current copy (occurrences = current.occurrences + 1))
    }

}

private object OscarNERAdapter {

  private lazy val oscar = new Oscar()

}

