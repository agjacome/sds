package es.uvigo.ei.sing.sds
package annotator

import scala.concurrent.Future

import entity._
import service.{ DrugEntity, DrugsService }

final class DrugsAnnotator extends AnnotatorAdapter {

  import context._

  lazy val drugs = new DrugsService

  override def annotate(article: Article): Future[Unit] =
    drugs.getEntities(article.content).flatMap(es => saveEntities(es, article.id.get)).map(_ => ())

  private def saveEntities(entities: Set[DrugEntity], articleId: Article.ID): Future[Set[(Keyword, Annotation)]] =
    Future.sequence { entities.map(e => saveEntity(e, articleId)) }

  private def saveEntity(entity: DrugEntity, articleId: Article.ID): Future[(Keyword, Annotation)] =
    for {
      normalized <- drugs.normalize(entity)
      keyword    <- getOrStoreKeyword(normalized, Drug)
      annotation <- annotationsDAO.insert(entity.toAnnotation(articleId, keyword.id.get))
    } yield (keyword, annotation)


}
