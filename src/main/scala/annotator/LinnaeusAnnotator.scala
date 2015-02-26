package es.uvigo.ei.sing.sds.annotator

import uk.ac.man.entitytagger.Mention

import es.uvigo.ei.sing.sds.entity._
import es.uvigo.ei.sing.sds.service.LinnaeusService

private[annotator] class LinnaeusAnnotator extends AnnotatorAdapter {

  import context._

  lazy val linnaeus = LinnaeusService()

  override protected def annotate(document : Document) =
    linnaeus obtainMentions document.text map {
      mentions => mentions foreach (saveMention(_, document.id.get))
    }

  private[this] def saveMention(mention : Mention, documentId : DocumentId) = {
    val normalized = linnaeus normalize mention
    val keywordId  = getOrStoreKeyword(normalized, Species)
    val annotation = Annotation(None, documentId, keywordId, mention.getText, mention.getStart, mention.getEnd)
    storeAnnotation(annotation)
  }

}

