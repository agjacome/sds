package es.uvigo.esei.tfg.smartdrugsearch.annotator

import scala.collection.JavaConversions._
import scala.concurrent.future

import uk.ac.cam.ch.wwmm.oscar.chemnamedict.entities.{ FormatType, ResolvedNamedEntity }
import uk.ac.cam.ch.wwmm.oscar.document.NamedEntity

import es.uvigo.esei.tfg.smartdrugsearch.entity._

private[annotator] class OscarNERAdapter extends NERAdapter {

  import context._
  import OscarNERAdapter._

  override protected def annotate(document : Document) =
    getNamedEntities(document.text) map { entities =>
      entities foreach { saveEntity(_, document) }
      Finished(document)
    }

  private[this] def getNamedEntities(text : String) =
    future { oscar.findResolvableEntities(text) }

  private[this] def saveEntity(entity : ResolvedNamedEntity, document : Document) = {
    val keyword    = getOrStoreNewKeyword(entity.getFirstChemicalStructure(FormatType.INCHI).getValue, Compound)
    val annotation = getAnnotation(entity.getNamedEntity, keyword, document)
    storeAnnotation(keyword, annotation)
  }

  private[this] def getAnnotation(e : NamedEntity, k : Keyword, d : Document) =
    Annotation(None, d.id.get, k.id.get, e.getSurface, e.getStart, e.getEnd)

}

private object OscarNERAdapter {

  import uk.ac.cam.ch.wwmm.oscar.Oscar

  private lazy val oscar = new Oscar()

}

