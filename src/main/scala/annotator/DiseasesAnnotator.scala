package es.uvigo.ei.sing.sds
package annotator

import scala.concurrent.Future

import entity._
import service.{ DiseaseEntity, DiseasesService }

final class DiseasesAnnotator extends AnnotatorAdapter {

  import context._

  lazy val diseases = new DiseasesService

  override def annotate(article: Article): Future[Unit] =
    diseases.getEntities(article.content).flatMap(es => saveEntities(es, article.id.get)).map(_ => ())

  private def saveEntities(entities: Set[DiseaseEntity], articleId: Article.ID): Future[Set[(Keyword, Annotation)]] =
    Future.sequence { entities.map(e => saveEntity(e, articleId)) }

  private def saveEntity(entity: DiseaseEntity, articleId: Article.ID): Future[(Keyword, Annotation)] =
    for {
      normalized <- diseases.normalize(entity)
      keyword    <- getOrStoreKeyword(normalized, Disease)
      annotation <- annotationsDAO.insert(entity.toAnnotation(articleId, keyword.id.get))
    } yield (keyword, annotation)

}
