package es.uvigo.ei.sing.sds
package annotator

import scala.concurrent.Future

import entity._
import service.{ ABNEREntity, ABNERService }

final class ABNERAnnotator extends AnnotatorAdapter {

  import context._

  lazy val abner = new ABNERService

  override def annotate(article: Article): Future[Unit] =
    abner.getEntities(article.content).flatMap(es => saveEntities(es, article.id.get)).map(_ => ())

  private def saveEntities(entities: Set[ABNEREntity], articleId: Article.ID): Future[Set[(Keyword, Annotation)]] =
    Future.sequence { entities.map(e => saveEntity(e, articleId)) }

  private def saveEntity(entity: ABNEREntity, articleId: Article.ID): Future[(Keyword, Annotation)] =
    for {
      normalized <- abner.normalize(entity)
      keyword    <- getOrStoreKeyword(normalized, entity.cat)
      annotation <- annotationsDAO.insert(entity.toAnnotation(articleId, keyword.id.get))
    } yield (keyword, annotation)

}
