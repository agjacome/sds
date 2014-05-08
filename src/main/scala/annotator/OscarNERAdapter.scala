package es.uvigo.esei.tfg.smartdrugsearch.annotator

import scala.concurrent.future

import uk.ac.cam.ch.wwmm.oscar.chemnamedict.entities.ResolvedNamedEntity
import uk.ac.cam.ch.wwmm.oscar.document.NamedEntity

import es.uvigo.esei.tfg.smartdrugsearch.entity._
import es.uvigo.esei.tfg.smartdrugsearch.util.OscarUtils

private[annotator] class OscarNERAdapter extends NERAdapter {

  import context._

  private lazy val oscar = OscarUtils()

  override protected def annotate(document : Document) =
    getNamedEntities(document.text) map { entities =>
      entities foreach { saveEntity(_, document) }
      Finished(document)
    }

  private[this] def getNamedEntities(text : String) =
    future { oscar.getNamedEntities(text) }

  private[this] def saveEntity(entity : ResolvedNamedEntity, document : Document) = {
    val keyword    = getOrStoreNewKeyword(oscar.normalize(entity), Compound)
    val annotation = getAnnotation(entity.getNamedEntity, keyword, document)
    storeAnnotation(keyword, annotation)
  }

  private[this] def getAnnotation(e : NamedEntity, k : Keyword, d : Document) =
    Annotation(None, d.id.get, k.id.get, e.getSurface, e.getStart, e.getEnd)

}

