package es.uvigo.esei.tfg.smartdrugsearch.annotator

import scala.concurrent.future

import uk.ac.man.entitytagger.Mention

import es.uvigo.esei.tfg.smartdrugsearch.entity._
import es.uvigo.esei.tfg.smartdrugsearch.util.LinnaeusUtils

private[annotator] class LinnaeusNERAdapter extends NERAdapter {

  import context._

  private lazy val linnaeus = LinnaeusUtils()

  override protected def annotate(document : Document) =
    obtainMentions(document.text) map { mentions =>
      mentions foreach { saveMention(_, document) }
      Finished(document)
    }

  private[this] def obtainMentions(text : String) =
    future { linnaeus obtainMentions text }

  private[this] def saveMention(mention : Mention, document : Document) = {
    val keyword    = getKeyword(linnaeus normalize mention)
    val annotation = getAnnotation(mention, keyword, document)
    storeAnnotation(keyword, annotation)
  }

  private[this] def getKeyword(normalized : String) =
    getOrStoreNewKeyword(normalized, Species)

  private[this] def getAnnotation(m : Mention, k : Keyword, d : Document) =
    Annotation(None, d.id.get, k.id.get, m.getText, m.getStart, m.getEnd)

}

