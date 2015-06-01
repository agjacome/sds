package es.uvigo.ei.sing.sds
package annotator

import scala.concurrent.Future

import uk.ac.man.entitytagger.Mention

import entity._
import service.LinnaeusService
import service.LinnaeusService.MentionOps

final class LinnaeusAnnotator extends AnnotatorAdapter {

  import context._

  lazy val linnaeus = new LinnaeusService

  override def annotate(article: Article): Future[Unit] =
    linnaeus.getMentions(article.content).flatMap(ms => saveMentions(ms, article.id.get)).map(_ => ())

  private def saveMentions(mentions: Set[Mention], articleId: Article.ID): Future[Set[(Keyword, Annotation)]] =
    Future.sequence { mentions.map(m => saveMention(m, articleId)) }

  private def saveMention(mention: Mention, articleId: Article.ID): Future[(Keyword, Annotation)] =
    for {
      normalized <- linnaeus.normalize(mention)
      keyword    <- getOrStoreKeyword(normalized, Species)
      annotation <- annotationsDAO.insert(mention.toAnnotation(articleId, keyword.id.get))
    } yield (keyword, annotation)

}
