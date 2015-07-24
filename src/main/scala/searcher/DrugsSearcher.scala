package es.uvigo.ei.sing.sds
package searcher

import scala.concurrent.Future

import play.api.libs.concurrent.Execution.Implicits.defaultContext

import entity._
import service.DrugsService

final class DrugsSearcher extends SearcherAdapter {

  lazy val drugs = new DrugsService

  override def search(query: String): Future[Set[Keyword.ID]] =
    for {
      mentions   <- drugs.getEntities(query)
      normalized <- Future.sequence(mentions.map(drugs.normalize))
      keywordIds <- searchNormalized(normalized)
    } yield keywordIds

}
