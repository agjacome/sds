package es.uvigo.ei.sing.sds
package annotator

import scala.concurrent.Future

import uk.ac.cam.ch.wwmm.oscar.chemnamedict.entities.ResolvedNamedEntity

import entity._
import service.OscarService
import service.OscarService.NamedEntityOps

final class OscarAnnotator extends AnnotatorAdapter {

  import context._

  lazy val oscar = new OscarService

  override def annotate(article: Article): Future[Unit] =
    oscar.getNamedEntities(article.content).flatMap(es => saveEntities(es, article.id.get)).map(_ => ())

  private def saveEntities(entities: Set[ResolvedNamedEntity], articleId: Article.ID): Future[Set[(Keyword, Annotation)]] =
    Future.sequence { entities.map(e => saveEntity(e, articleId)) }

  private def saveEntity(entity: ResolvedNamedEntity, articleId: Article.ID): Future[(Keyword, Annotation)] =
    for {
      normalized <- oscar.normalize(entity)
      keyword    <- getOrStoreKeyword(normalized, Compound)
      annotation <- annotationsDAO.insert(entity.toAnnotation(articleId, keyword.id.get))
    } yield (keyword, annotation)

}
