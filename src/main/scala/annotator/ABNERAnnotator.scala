package es.uvigo.ei.sing.sds.annotator


import es.uvigo.ei.sing.sds.entity._
import es.uvigo.ei.sing.sds.service.{ ABNEREntity, ABNERService }

private[annotator] class ABNERAnnotator extends AnnotatorAdapter {

  import context._

  lazy val abner = ABNERService()

  override protected def annotate(document : Document) =
    abner getEntities document.text map {
      entities => entities foreach (saveEntity(_, document.id.get))
    }

  private[this] def saveEntity(entity : ABNEREntity, documentId : DocumentId) = {
    val normalized = abner normalize entity
    val keywordId  = getOrStoreKeyword(normalized, entity.cat)
    val annotation = Annotation(None, documentId, keywordId, entity.txt, entity.start, entity.end)
    storeAnnotation(annotation)
  }

}

