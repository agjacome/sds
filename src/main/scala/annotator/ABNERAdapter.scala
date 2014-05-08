package es.uvigo.esei.tfg.smartdrugsearch.annotator

import scala.concurrent.future

import es.uvigo.esei.tfg.smartdrugsearch.entity._
import es.uvigo.esei.tfg.smartdrugsearch.util.{ ABNEREntity, ABNERUtils }

private[annotator] class ABNERAdapter extends NERAdapter {

  import context._

  private lazy val abner = ABNERUtils()

  override protected def annotate(document : Document) =
    getEntities(document.text) map { entities =>
      entities foreach { saveEntity(_, document) }
      Finished(document)
    }

  private[this] def getEntities(text : String) =
    future { abner getEntities text }

  private[this] def saveEntity(entity : ABNEREntity, document : Document) = {
    val keyword    = getOrStoreNewKeyword(abner normalize entity, entity.cat)
    val annotation = getAnnotation(entity, keyword, document)
    storeAnnotation(keyword, annotation)
  }

  private[this] def getAnnotation(e : ABNEREntity, k : Keyword, d : Document) =
    Annotation(None, d.id.get, k.id.get, e.str, e.start, e.end)

}

